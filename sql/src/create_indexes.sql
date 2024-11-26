CREATE INDEX foodorderTimestamp ON FoodOrder USING BTREE(orderTimestamp);

CREATE INDEX itemsInOrderName ON ItemsInOrder USING BTREE(itemName);

CREATE INDEX userRole ON Users USING BTREE(role);

CREATE INDEX itemsPrice ON Items USING BTREE(price);

