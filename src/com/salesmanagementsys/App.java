package com.salesmanagementsys;

import org.eclipse.swt.SWT;


import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.FillLayout;  // Added import
import org.eclipse.swt.custom.StackLayout;  // Added import
import com.salesmanagementsys.view.*;
import com.salesmanagementsys.controller.*;
import com.salesmanagementsys.model.DatabaseManager;
import java.sql.SQLException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class App {
	public static void main(String[] args) {
	    Display display = new Display();
	    
	 // At the beginning of main method
	    try {
	        File logFile = new File(System.getProperty("user.home") + "/amazingdessertbar-startup.log");
	        FileWriter fw = new FileWriter(logFile);
	        fw.write("Application starting at: " + new java.util.Date() + "\n");
	        fw.write("Java version: " + System.getProperty("java.version") + "\n");
	        fw.write("Working directory: " + System.getProperty("user.dir") + "\n");
	        fw.write("java.library.path: " + System.getProperty("java.library.path") + "\n");
	        
	        // List all system properties
	        fw.write("\n--- System Properties ---\n");
	        System.getProperties().forEach((k, v) -> {
	            try {
	                fw.write(k + ": " + v + "\n");
	            } catch (IOException e) {}
	        });
	        
	        fw.flush();
	        fw.close();
	    } catch (Exception e) {
	        // Ignore exceptions during logging
	    }

	    try {
	        // Create DatabaseManager
	        DatabaseManager dbManager = new DatabaseManager();       
	       
	        
	        // Create LoginController and LoginView
	        LoginController loginController = new LoginController(dbManager);
	        LoginView loginView = new LoginView(display, loginController);
	        loginView.open();       
	      	        
	        // Wait for login to complete or window to be closed
	        while (!loginView.getShell().isDisposed()) {
	            if (!display.readAndDispatch()) {
	                display.sleep();
	            }
	        }
	        
	        // Continue only if login was successful
	        if (loginController.isLoggedIn()) {
	            // Create main shell and content area first
	            Shell mainShell = new Shell(display, SWT.SHELL_TRIM);
	            mainShell.setLayout(new FillLayout());
	            
	            // Create content area with StackLayout that will hold all views
	            Composite contentArea = new Composite(mainShell, SWT.NONE);
	            StackLayout stackLayout = new StackLayout();
	            contentArea.setLayout(stackLayout);
	            
	            // Create controllers
	            CustomerController customerController = new CustomerController(dbManager);
	            OrderController orderController = new OrderController(null, dbManager);
	            PreOrderController preOrderController = new PreOrderController(null, dbManager);
	            StaffController staffController = new StaffController(null, dbManager);
	            ItemController itemController = new ItemController(dbManager);
	            StockPurchaseController stockPurchaseController = new StockPurchaseController(dbManager);
	            
	         // connection code 
	            customerController.setOrderController(orderController);
	            customerController.setPreOrderController(preOrderController);
	            
	            // Create views with the contentArea as their parent
	            CustomerView customerView = new CustomerView(contentArea, customerController);
	            CustomerListView customerListView = new CustomerListView(contentArea, customerController);
	            customerController.setCustomerView(customerView);
	            customerController.setCustomerListView(customerListView);
	            
	            OrderView orderView = new OrderView(contentArea, orderController);
	            OrderListView orderListView = new OrderListView(contentArea, orderController);
	            orderController.setView(orderView);	            
	         
	            orderController.setListView(orderListView);
	            orderController.refreshOrderList();
	            
	            PreOrderView preOrderView = new PreOrderView(contentArea, preOrderController);
	            PreOrderListView preOrderListView = new PreOrderListView(contentArea, preOrderController);
	            preOrderController.setView(preOrderView);
	            
	            preOrderController.setListView(preOrderListView);
	            preOrderController.refreshPreOrderList();
	            
	            StaffView staffView = new StaffView(contentArea, staffController);
	            StaffListView staffListView = new StaffListView(contentArea, staffController);
	            staffController.setView(staffView);
	            
	            ItemView itemView = new ItemView(contentArea, itemController);
	            ItemListView itemListView = new ItemListView(contentArea, itemController);
	            
	            StockPurchaseView stockPurchaseView = new StockPurchaseView(contentArea, stockPurchaseController);
	            StockPurchaseListView stockPurchaseListView = new StockPurchaseListView(contentArea, stockPurchaseController);
	            	           
	            ReportController reportController = new ReportController(null, dbManager);
	            ReportView reportView = new ReportView(contentArea, reportController);
	            reportController.setView(reportView);
	            
	         // added after other view creations, Ordering is crucial
	            TableManagementView tableManagementView = new TableManagementView(contentArea, dbManager);

	            
	            // Create MainView with the shell and all views	         
	            MainView mainView = new MainView(mainShell, contentArea, stackLayout,
	            	    customerView, customerListView,
	            	    orderView, orderListView,
	            	    preOrderView, preOrderListView,
	            	    staffView, staffListView,
	            	    itemView, itemListView,
	            	    reportView,
	            	    stockPurchaseView, stockPurchaseListView,
	            	    tableManagementView,
	            	    customerController, orderController,
	            	    preOrderController, staffController,
	            	    itemController, stockPurchaseController, 
	            	    reportController,
	            	    loginController.getLoggedInStaff());

	            mainView.open();

	            while (!mainView.getShell().isDisposed()) {
	                if (!display.readAndDispatch()) {
	                    display.sleep();
	                }
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Database initialization error: " + e.getMessage());
	    } finally {
	        display.dispose();
	    }	    
	   
		}	 
	}
	

