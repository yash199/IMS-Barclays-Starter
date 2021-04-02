package com.barclays.ims;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Class responsible for the functionality of the menu system.
 * You may build this out in any way you wish, provided that you stick to CLI.
 * GUIs, such as swing, are not permitted.
 */
public class MenuSystem {

    public static final Logger LOGGER = LogManager.getLogger();

    public Connection connection = null;
    public Statement statement = null;

    public ScannerUtils scanner = new ScannerUtils();

    HashMap<Integer, String> menuOptions = new HashMap<>(3);
    HashMap<Integer, String> subMenuOptions = new HashMap<>(7);

    public MenuSystem() {
        init();
    }

    public void init() {
        DbUtils.connect();
        DbUtils.getInstance().init("src/main/resources/sql-schema.sql", "src/main/resources/sql-data.sql");

        try {
            connection = DbUtils.getInstance().getConnection();
            statement = connection.createStatement();
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    public void run() {
        while(true) {
            menuOptions();
        }
    }

    private void subMenuOptions(int option) {
        if(option == 0) {
            System.exit(0);
        }
        else if(option >=1 && option <=3) {
            String optionStr = menuOptions.get(option);
            System.out.println("Select an operation on " + optionStr);
            System.out.println("1 - ADD");
            System.out.println("2 - VIEW");
            System.out.println("3 - UPDATE");
            System.out.println("4 - DELETE");
            System.out.println("5 - ADD ITEM");
            System.out.println("6 - DELETE ITEM");
            System.out.println("7 - COST");
            subMenuOptions.put(1, "ADD");
            subMenuOptions.put(2, "VIEW");
            subMenuOptions.put(3, "UPDATE");
            subMenuOptions.put(4, "DELETE");
            subMenuOptions.put(5, "ADD ITEM");
            subMenuOptions.put(6, "DELETE ITEM");
            subMenuOptions.put(7, "COST");
            int actionVal = scanner.getLong().intValue();
            if(subMenuOptions.containsKey(actionVal)) {
                action(optionStr, subMenuOptions.get(actionVal));
            }
            else {
                System.out.println("Invalid Entry: Try Again");
                menuOptions();
            }
        }
        else {
            System.out.println("Invalid Entry: Try Again");
            menuOptions();
        }
    }

    private void action(String optionStr, String actionVal) {
        switch(optionStr) {
            case "CUSTOMER":
                if(actionVal == "ADD") { addCustomer(); }
                else if(actionVal == "VIEW") { viewCustomer(); }
                else if(actionVal == "UPDATE") { updateCustomer(); }
                else if(actionVal == "DELETE") { deleteCustomer(); }
                else { System.out.println("Invalid Entry: Try Again"); menuOptions(); }
                break;
            case "ITEM":
                if(actionVal == "ADD") { addItem(); }
                else if(actionVal == "VIEW") { viewItem(); }
                else if(actionVal == "UPDATE") { updateItem(); }
                else if(actionVal == "DELETE") { deleteItem(); }
                else { System.out.println("Invalid Entry: Try Again"); menuOptions(); }
                break;
            case "ORDER":
                if(actionVal == "ADD") { createOrder(); }
                else if(actionVal == "VIEW") { viewOrder(); }
                else if(actionVal == "DELETE") { deleteOrder(); }
                else if(actionVal == "ADD ITEM") { addItemToOrder(); }
                else if(actionVal == "DELETE ITEM") { deleteItemFromOrder(); }
                else if(actionVal == "COST") { calculateCost(); }
                else { System.out.println("Invalid Entry: Try Again"); menuOptions(); }
                break;
        }
    }

    private void calculateCost() {
        System.out.println("Please enter an Order ID");
        int order_id = scanner.getLong().intValue();
        List<Integer> itemArray = new ArrayList<Integer>();
        double total = 0;

        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM ims.ORDERED_ITEMS WHERE ORDER_ID = " + order_id);
            while(rs.next()){
                itemArray.add(rs.getInt("ITEM_ID"));
            }
            for(int i:itemArray){
                String findCostofItem = "SELECT (COST) FROM ims.ITEM WHERE ID = " + i;
                rs = statement.executeQuery(findCostofItem);
                while (rs.next()){
                    BigDecimal itemCost = rs.getBigDecimal("COST");
                    total += itemCost.doubleValue();
                }
            }
            System.out.println("Total Cost of Order: " + total);
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void deleteItemFromOrder() {
        System.out.println("Deleting an Item from Order");
        System.out.println("Please enter an Order ID");
        int order_id = scanner.getLong().intValue();
        System.out.println("Please enter an Item ID");
        int item_id = scanner.getLong().intValue();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ims.ORDERED_ITEMS WHERE ORDER_ID = ? AND ITEM_ID = ?");
            preparedStatement.setInt(1, order_id);
            preparedStatement.setInt(2, item_id);
            preparedStatement.executeUpdate();
            System.out.println("Item Deleted From Order");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void deleteOrder() {
        System.out.println("Deleting Order");
        System.out.println("Enter Order ID");
        int order_id = scanner.getLong().intValue();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ims.ORDERS WHERE ID = ?");
            preparedStatement.setInt(1, order_id);
            preparedStatement.executeUpdate();
            System.out.println("Order Deleted");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void viewOrder() {
        System.out.println("Showing all Orders - Customers");
        try {
            ResultSet rs = statement.executeQuery("SELECT * from ims.CUSTOMER where ID in (SELECT CUSTOMER_ID FROM ims.ORDERS)");
            printData(rs);
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
        System.out.println("Showing all Orders - items");
        try {
            ResultSet rs = statement.executeQuery("SELECT * from ims.ITEM where ID in (SELECT ITEM_ID FROM ims.ORDERED_ITEMS)");
            printData(rs);
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void createOrder() {
        System.out.println("Creating Order");
        System.out.println("Please enter Customer ID");
        int customer_id = scanner.getLong().intValue();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ims.ORDERS (CUSTOMER_ID) VAlUES (?)");
            preparedStatement.setInt(1, customer_id);
            preparedStatement.executeUpdate();
            System.out.println("Order Created");
            System.out.println("Please enter the items for this order");
            addItemToOrder();
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void addItemToOrder() {
        System.out.println("Adding Item to Order");
        System.out.println("Please enter an Order ID");
        int order_id = scanner.getLong().intValue();
        System.out.println("Please enter an Item ID");
        int item_id = scanner.getLong().intValue();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ims.ORDERED_ITEMS (ORDER_ID, ITEM_ID) VALUES(?, ?)");
            preparedStatement.setInt(1, order_id);
            preparedStatement.setInt(2, item_id);
            preparedStatement.executeUpdate();
            System.out.println("Item Added to Order");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void deleteItem() {
        System.out.println("Deleting Item");
        System.out.println("Enter an Item ID");
        int item_id = scanner.getLong().intValue();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ims.ITEM WHERE ID = ?");
            preparedStatement.setInt(1, item_id);
            preparedStatement.executeUpdate();
            System.out.println("Customer Deleted");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void updateItem() {
        System.out.println("Updating Item");
        System.out.println("Enter Item ID");
        int item_id = scanner.getLong().intValue();
        System.out.println("Please enter an Item name");
        String name = scanner.getString();
        System.out.println("Please enter an Item cost");
        Double cost = scanner.getDouble();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ims.ITEM SET NAME = ?, COST = ? WHERE ID = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, cost);
            preparedStatement.setInt(3, item_id);
            preparedStatement.executeUpdate();
            System.out.println("Item Updated");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void viewItem() {
        System.out.println("View All Items");
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM ims.ITEM");
            printData(rs);
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void addItem() {
        System.out.println("Adding Item");
        System.out.println("Please enter an Item name");
        String name = scanner.getString();
        System.out.println("Please enter Item cost");
        Double cost = scanner.getDouble();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ims.ITEM (NAME, COST) VAlUES (?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, cost);
            preparedStatement.executeUpdate();
            System.out.println("Item Added");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void deleteCustomer() {
        System.out.println("Deleting Customer");
        System.out.println("Enter Customer ID");
        int customer_id = scanner.getLong().intValue();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ims.CUSTOMER WHERE ID = ?");
            preparedStatement.setInt(1, customer_id);
            preparedStatement.executeUpdate();
            System.out.println("Customer Deleted");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void updateCustomer() {
        System.out.println("Updating Customer");
        System.out.println("Enter Customer ID");
        int customer_id = scanner.getLong().intValue();
        System.out.println("Enter Customer Name");
        String name = scanner.getString();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ims.CUSTOMER SET NAME = ? WHERE ID = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, customer_id);
            preparedStatement.executeUpdate();
            System.out.println("Customer Updated");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void viewCustomer() {
        System.out.println("View All Customers");
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM ims.CUSTOMER");
            printData(rs);

        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void printData(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        for(int i = 1; i<=columnsNumber; i++) {
            System.out.print(rsmd.getColumnName(i) + " ");
        }
        System.out.println();
        while (rs.next()) {
            for(int i = 1 ; i <= columnsNumber; i++){
                System.out.print(rs.getString(i) + " ");
            }
            System.out.println();
        }
    }

    private void addCustomer() {
        System.out.println("Adding Customer");
        System.out.println("Enter Customer name");
        String name = scanner.getString();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ims.CUSTOMER (NAME) VAlUES (?)");
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            System.out.println("Customer Added Successfully");
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    private void menuOptions() {
        System.out.println("Inventory Management System - SELECT AN OPTION NUMBER FROM BELOW");
        System.out.println("1 - CUSTOMER");
        System.out.println("2 - ITEM");
        System.out.println("3 - ORDER");
        System.out.println("0 - EXIT FROM MENU");
        menuOptions.put(1, "CUSTOMER");
        menuOptions.put(2, "ITEM");
        menuOptions.put(3, "ORDER");
        subMenuOptions(scanner.getLong().intValue());
    }
}
