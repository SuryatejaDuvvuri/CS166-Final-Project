/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class PizzaStore {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of PizzaStore
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end PizzaStore

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            PizzaStore.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      PizzaStore esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the PizzaStore object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new PizzaStore (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Menu");
                System.out.println("4. Place Order"); //make sure user specifies which store
                System.out.println("5. View Full Order ID History");
                System.out.println("6. View Past 5 Order IDs");
                System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                System.out.println("8. View Stores"); 

                //**the following functionalities should only be able to be used by drivers & managers**
                System.out.println("9. Update Order Status");

                //**the following functionalities should ony be able to be used by managers**
                System.out.println("10. Update Menu");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql); break;
                   case 2: updateProfile(esql); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break; // Pass the user
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql); break;
                   case 10: updateMenu(esql); break;
                   case 11: updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(PizzaStore esql) {
    try {
        System.out.print("Enter username: ");
        String username = in.readLine();
        System.out.print("Enter password: ");
        String password = in.readLine();
        System.out.print("Enter phone number: ");
        String phone = in.readLine();
        System.out.print("Enter your role(Customer, Manager, Driver): ");
        String role = in.readLine();
        System.out.print("Enter your favorite item: ");
        String favoriteItem = in.readLine();

        String query = String.format("INSERT INTO Users (login, password, role, favoriteItems, phoneNum) " +
                                     "VALUES ('%s', '%s', '%s', '%s', '%s');", username, password, role, favoriteItem, phone);

        esql.executeUpdate(query);
        System.out.println("User successfully created!");
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
   }

   //end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(PizzaStore esql) {
    try {
        System.out.print("Enter username: ");
        String username = in.readLine();
        System.out.print("Enter password: ");
        String password = in.readLine();

        String query = String.format("SELECT role FROM Users WHERE login = '%s' AND password = '%s';", username, password);

        List<List<String>> result = esql.executeQueryAndReturnResult(query);
        if (result.size() > 0) {
            System.out.println("Login successful. Welcome, " + username + "!");
            return username; // Return the username to maintain the session
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return null;
        }
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        return null;
    }
   }

   //end

// Rest of the functions definition go in here

   public static void viewProfile(PizzaStore esql) {
    try {
        System.out.print("Enter your username: ");
        String username = in.readLine();

        String query = String.format(
            "SELECT login, favoriteItems, phoneNum FROM Users WHERE login = '%s';", username);

        List<List<String>> result = esql.executeQueryAndReturnResult(query);

        if (result.size() > 0) {
            System.out.println("Profile Details:");
            System.out.println("Username: " + result.get(0).get(0));
            System.out.println("Favorite Items: " + result.get(0).get(1));
            System.out.println("Phone Number: " + result.get(0).get(2));
        } else {
            System.out.println("No profile found for the given username.");
        }
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
   }

   public static void updateProfile(PizzaStore esql) {
    try {
        System.out.print("Enter your username: ");
        String username = in.readLine();

        System.out.println("What would you like to update?");
        System.out.println("1. Favorite Items");
        System.out.println("2. Phone Number");
        System.out.println("3. Password");
        int choice = readChoice();

        switch (choice) {
            case 1:
                System.out.print("Enter your new favorite items: ");
                String newFavoriteItems = in.readLine();
                String updateFavoriteQuery = String.format(
                    "UPDATE Users SET favoriteItems = '%s' WHERE login = '%s';",
                    newFavoriteItems, username);
                esql.executeUpdate(updateFavoriteQuery);
                System.out.println("Favorite items updated successfully.");
                break;

            case 2:
                System.out.print("Enter your new phone number: ");
                String newPhoneNum = in.readLine();
                String updatePhoneQuery = String.format(
                    "UPDATE Users SET phoneNum = '%s' WHERE login = '%s';",
                    newPhoneNum, username);
                esql.executeUpdate(updatePhoneQuery);
                System.out.println("Phone number updated successfully.");
                break;

            default:
                System.out.println("Invalid choice. Please try again.");
        }
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
   }

   public static void viewMenu(PizzaStore esql) {
    try {
        System.out.println("View Menu Options:");
        System.out.println("1. View all items");
        System.out.println("2. Filter by type");
        System.out.println("3. Filter by price");
        System.out.println("4. Sort by price");

        switch (readChoice()) {
            case 1:
                esql.executeQueryAndPrintResult("SELECT * FROM Items;");
                break;
            case 2:
                System.out.print("Enter type of item (e.g., drinks, sides): ");
                String type = in.readLine().trim().toLowerCase();
                esql.executeQueryAndPrintResult("SELECT * FROM Items WHERE LOWER(typeOfItem) = ' " + type + "';");
                break; //case 2 is not working debugging required
            case 3:
                System.out.print("Enter maximum price: ");
                double priceLimit = Double.parseDouble(in.readLine());
                esql.executeQueryAndPrintResult("SELECT * FROM Items WHERE price <= " + priceLimit + ";");
                break;
            case 4:
                System.out.println("Sort by price:");
                System.out.println("1. Lowest to Highest");
                System.out.println("2. Highest to Lowest");
                int sortChoice = readChoice();
                String order = (sortChoice == 1) ? "ASC" : "DESC";
                esql.executeQueryAndPrintResult("SELECT * FROM Items ORDER BY price " + order + ";");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}

   public static void placeOrder(PizzaStore esql, String authorisedUser) {
    try {
        // Step 1: Get store ID
        System.out.print("Enter store ID: ");
        int storeID = Integer.parseInt(in.readLine());

        double totalPrice = 0.0;
        List<String> items = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        // Step 2: Collect items and quantities
        while (true) {
            System.out.print("Enter item name (or 'done' to finish): ");
            String itemName = in.readLine().trim();
            if (itemName.equalsIgnoreCase("done")) break;

            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(in.readLine().trim());

            // Validate item name and get price
            String query = String.format("SELECT price FROM Items WHERE itemName = '%s';", itemName);
            List<List<String>> result = esql.executeQueryAndReturnResult(query);

            if (!result.isEmpty()) {
                double price = Double.parseDouble(result.get(0).get(0));
                totalPrice += price * quantity;
                items.add(itemName);
                quantities.add(quantity);
            } else {
                System.out.println("Item not found. Please try again.");
            }
        }

        // Check if no items were added
        if (items.isEmpty()) {
            System.out.println("No items in the order. Aborting.");
            return;
        }

        // Step 3: Determine the next orderID
        String maxOrderIDQuery = "SELECT COALESCE(MAX(orderID), 0) + 1 FROM FoodOrder;";
        List<List<String>> orderIDResult = esql.executeQueryAndReturnResult(maxOrderIDQuery);
        int orderID = Integer.parseInt(orderIDResult.get(0).get(0));

        // Step 4: Insert order into FoodOrder
        String insertOrder = String.format(
            "INSERT INTO FoodOrder (orderID, login, storeID, totalPrice, orderTimestamp, orderStatus) " +
            "VALUES (%d, '%s', %d, %.2f, NOW(), 'placed');",
            orderID, authorisedUser, storeID, totalPrice);
        esql.executeUpdate(insertOrder);

        // Step 5: Insert items into ItemsInOrder
        for (int i = 0; i < items.size(); i++) {
            String insertItem = String.format(
                "INSERT INTO ItemsInOrder (orderID, itemName, quantity) VALUES (%d, '%s', %d);",
                orderID, items.get(i), quantities.get(i));
            esql.executeUpdate(insertItem);
        }

        // Step 6: Confirm success
        System.out.println("Order placed successfully!");
        System.out.printf("Total Price: $%.2f\n", totalPrice);
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}


   public static void viewAllOrders(PizzaStore esql)
   {
        try
        {
     

              System.out.println("Enter your username: ");
              String username = in.readLine();

              List< List<String> > result = esql.executeQueryAndReturnResult(String.format("SELECT role FROM Users WHERE login = '%s';", username));

              if(result.get(0).get(0).equalsIgnoreCase("Customer"))
              {
                    esql.executeQueryAndPrintResult("SELECT OrderID from FoodOrder JOIN Users ON Users.login = FoodOrder.login WHERE Users.role = 'Customer';");
              }
              else
              {
                esql.executeQueryAndPrintResult("SELECT OrderID from FoodOrder ORDER BY orderStatus DESC;");
              }

        }
        catch(Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
   }
   public static void viewRecentOrders(PizzaStore esql)
   {
        try
        {
              System.out.println("Enter your username: ");
              String username = in.readLine();

              esql.executeQueryAndPrintResult(String.format("SELECT OrderID from FoodOrder JOIN Users ON Users.login = FoodOrder.login WHERE Users.login = '%s' ORDER BY orderTimestamp DESC LIMIT 5;", username));

        }
        catch(Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
   }
   
   public static void viewOrderInfo(PizzaStore esql)
   {
        try
        {
              System.out.println("Enter your username: ");
              String username = in.readLine();
              System.out.println("Enter your OrderID: ");
              int orderID = Integer.parseInt(in.readLine());

              List< List<String> > result = esql.executeQueryAndReturnResult(String.format("SELECT role FROM Users WHERE login = '%s';", username));

              if(result.get(0).get(0).equalsIgnoreCase("Customer"))
              {
                   List<List <String> > info = esql.executeQueryAndReturnResult(String.format("SELECT * FROM FoodOrder WHERE FoodOrder.orderID = %d AND Users.login = %s", orderID, username) );
              }
              else
              {
                List<List <String> > info = esql.executeQueryAndReturnResult(String.format("SELECT * FROM FoodOrder WHERE FoodOrder.orderID = %d;", orderID));
              }

              esql.executeQueryAndPrintResult(String.format("SELECT * FROM itemsInOrder WHERE orderID = %d", orderID));


        }
        catch(Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
   }
   public static void viewStores(PizzaStore esql)
   {
        try
        {

              esql.executeQueryAndPrintResult("SELECT DISTINCT * FROM Store;");

        }
        catch(Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
   }
   public static void updateOrderStatus(PizzaStore esql) {
    try {
        System.out.print("Enter your username: ");
        String username = in.readLine();

        // Check if the user is authorized directly in the query
        String roleQuery = String.format(
            "SELECT COUNT(*) FROM Users WHERE login = '%s' AND (LOWER(role) = 'manager' OR LOWER(role) = 'driver');",
            username
        );
        int authorizedCount = esql.executeQuery(roleQuery);

        if (authorizedCount > 0) {
            System.out.print("Enter the orderID: ");
            int orderID = Integer.parseInt(in.readLine());

            System.out.print("Enter the new Order Status (incomplete, in progress, or complete): ");
            String orderStatus = in.readLine().trim().toLowerCase();

            String updateQuery = String.format(
                "UPDATE FoodOrder SET orderStatus = '%s' WHERE orderID = %d;",
                orderStatus, orderID
            );
            esql.executeUpdate(updateQuery);

            System.out.println("Order Status Updated Successfully!");
        } else {
            System.out.println("Access denied. Only Managers or Drivers can update order status.");
        }
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}


   public static void updateMenu(PizzaStore esql) {
    try {
        System.out.print("Enter your username: ");
        String username = in.readLine();

        // Check if the user is a Manager
        String roleQuery = String.format(
            "SELECT COUNT(*) FROM Users WHERE login = '%s' AND LOWER(role) = 'manager';",
            username
        );
        int isManager = esql.executeQuery(roleQuery);

        if (isManager > 0) {
            System.out.println("What would you like to update?");
            System.out.println("1. Update existing item");
            System.out.println("2. Add item");
            int choice = readChoice();

            switch (choice) {
                case 1:
                    System.out.print("Enter the existing item name: ");
                    String oldItemName = in.readLine();
                    System.out.print("Enter your new item name: ");
                    String newItemName = in.readLine();
                    System.out.print("Enter your new ingredients: ");
                    String newIngredients = in.readLine();
                    System.out.print("Enter the new type of item (e.g., drinks, sides): ");
                    String newType = in.readLine().trim().toLowerCase();
                    System.out.print("Enter the new price: ");
                    double priceLimit = Double.parseDouble(in.readLine());
                    System.out.print("Enter the description: ");
                    String description = in.readLine();

                    String updateQuery = String.format(
                        "UPDATE Items SET itemName = '%s', ingredients = '%s', typeOfItem = '%s', price = %.2f, description = '%s' WHERE itemName = '%s';",
                        newItemName, newIngredients, newType, priceLimit, description, oldItemName
                    );
                    esql.executeUpdate(updateQuery);
                    System.out.println("Menu updated successfully.");
                    break;

                case 2:
                    System.out.print("Enter your new item name: ");
                    newItemName = in.readLine();
                    System.out.print("Enter your new ingredients: ");
                    newIngredients = in.readLine();
                    System.out.print("Enter the new type of item (e.g., drinks, sides): ");
                    newType = in.readLine().trim().toLowerCase();
                    System.out.print("Enter the new price: ");
                    priceLimit = Double.parseDouble(in.readLine());
                    System.out.print("Enter the description: ");
                    description = in.readLine();

                    String newQuery = String.format(
                        "INSERT INTO Items(itemName, ingredients, typeOfItem, price, description) VALUES ('%s', '%s', '%s', %.2f, '%s');",
                        newItemName, newIngredients, newType, priceLimit, description
                    );
                    esql.executeUpdate(newQuery);
                    System.out.println("Menu updated successfully.");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } else {
            System.out.println("Access denied. Only Managers can update the menu.");
        }
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}

   public static void updateUser(PizzaStore esql) {
    try {
        System.out.print("Enter your username: ");
        String username = in.readLine();

        // Check if the user is a Manager
        String roleQuery = String.format(
            "SELECT COUNT(*) FROM Users WHERE login = '%s' AND LOWER(role) = 'manager';",
            username
        );
        int isManager = esql.executeQuery(roleQuery);

        if (isManager > 0) {
            System.out.print("Enter the current username to update: ");
            String oldName = in.readLine();

            System.out.println("1. Change role");
            System.out.println("2. Change password");
            System.out.println("3. Change phone number");
            System.out.println("4. Change favorite item");
            System.out.println("5. Change username");
            int choice = readChoice();

            String query = "";
            switch (choice) {
                case 1:
                    System.out.print("Enter the new role: ");
                    String newRole = in.readLine();
                    query = String.format("UPDATE Users SET role = '%s' WHERE login = '%s';", newRole, oldName);
                    break;
                case 2:
                    System.out.print("Enter the new password: ");
                    String newPassword = in.readLine();
                    query = String.format("UPDATE Users SET password = '%s' WHERE login = '%s';", newPassword, oldName);
                    break;
                case 3:
                    System.out.print("Enter the new phone number: ");
                    String newNumber = in.readLine();
                    query = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s';", newNumber, oldName);
                    break;
                case 4:
                    System.out.print("Enter the new favorite item: ");
                    String newFavorite = in.readLine();
                    query = String.format("UPDATE Users SET favoriteItems = '%s' WHERE login = '%s';", newFavorite, oldName);
                    break;
                case 5:
                    System.out.print("Enter the new username: ");
                    String newName = in.readLine();
                    query = String.format("UPDATE Users SET login = '%s' WHERE login = '%s';", newName, oldName);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            esql.executeUpdate(query);
            System.out.println("User Updated Successfully!");
        } else {
            System.out.println("Access denied. Only Managers can update user information.");
        }
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}


}//end PizzaStore

