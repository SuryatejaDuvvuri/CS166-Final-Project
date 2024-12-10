-- 1. Reduce Item Stock on Order Placement
DELIMITER $$

-- Stored Procedure to reduce stock
CREATE PROCEDURE ReduceStockOnOrder(
    IN itemName VARCHAR(255),
    IN quantity INT
)
BEGIN
    UPDATE Items
    SET stock = stock - quantity
    WHERE itemName = itemName;

    -- Ensure stock doesn't go negative
    IF (SELECT stock FROM Items WHERE itemName = itemName) < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Not enough stock for the item';
    END IF;
END$$

-- Trigger to call procedure after inserting an item into ItemsInOrder
CREATE TRIGGER ReduceStockTrigger
AFTER INSERT ON ItemsInOrder
FOR EACH ROW
BEGIN
    CALL ReduceStockOnOrder(NEW.itemName, NEW.quantity);
END$$

DELIMITER ;

-- 2. Automatically Update Incomplete Orders
DELIMITER $$

-- Stored Procedure to mark inactive orders
CREATE PROCEDURE MarkInactiveOrders()
BEGIN
    UPDATE FoodOrder
    SET orderStatus = 'incomplete'
    WHERE orderStatus NOT IN ('complete', 'cancelled')
      AND TIMESTAMPDIFF(DAY, orderTimestamp, NOW()) > 7;
END$$

-- Trigger to execute the procedure on a specific event (optional: manual trigger)
CREATE EVENT CheckInactiveOrders
ON SCHEDULE EVERY 1 DAY
DO
    CALL MarkInactiveOrders();$$

DELIMITER ;

-- 3. Log User Updates for Audit
DELIMITER $$

-- Trigger to log updates on Users table
CREATE TRIGGER LogUserUpdates
AFTER UPDATE ON Users
FOR EACH ROW
BEGIN
    INSERT INTO UserLogs (userID, changeTimestamp, oldData, newData)
    VALUES (OLD.login, NOW(), OLD.role, NEW.role);
END$$

DELIMITER ;

-- 4. Automatically Recalculate Total Order Price on Items Update
DELIMITER $$

-- Stored Procedure to recalculate total price
CREATE PROCEDURE RecalculateOrderTotal(
    IN orderID INT
)
BEGIN
    UPDATE FoodOrder
    SET totalPrice = (
        SELECT SUM(i.price * io.quantity)
        FROM ItemsInOrder io
        JOIN Items i ON io.itemName = i.itemName
        WHERE io.orderID = orderID
    )
    WHERE orderID = orderID;
END$$

-- Trigger to invoke recalculation after an item is added to an order
CREATE TRIGGER RecalculateTotalTrigger
AFTER INSERT ON ItemsInOrder
FOR EACH ROW
BEGIN
    CALL RecalculateOrderTotal(NEW.orderID);
END$$

DELIMITER ;

-- 5. Prevent Deletion of Items with Active Orders
DELIMITER $$

-- Trigger to prevent deletion of items if they are in active orders
CREATE TRIGGER PreventItemDeletion
BEFORE DELETE ON Items
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM ItemsInOrder WHERE itemName = OLD.itemName) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete item as it is part of an active order';
    END IF;
END$$

DELIMITER ;
