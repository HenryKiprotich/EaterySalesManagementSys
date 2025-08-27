package com.salesmanagementsys.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import com.salesmanagementsys.controller.*;
import com.salesmanagementsys.model.Staff;
import com.salesmanagementsys.model.AccessRightManager;
import org.eclipse.swt.graphics.Rectangle;

public class MainView {
    private Shell shell;
    private Composite contentArea;
    private StackLayout stackLayout;
    private Staff loggedInStaff; 
    
    // View references
    private CustomerView customerView;
    private CustomerListView customerListView;
    private OrderView orderView;
    private OrderListView orderListView;
    private PreOrderView preOrderView;
    private PreOrderListView preOrderListView;
    private StaffView staffView;
    private StaffListView staffListView;
    private ItemView itemView;
    private ItemListView itemListView;
    private StockPurchaseView stockPurchaseView;
    private StockPurchaseListView stockPurchaseListView; 
    private ReportView reportView;
    private TableManagementView tableManagementView;

    
    // Controller references
    private CustomerController customerController;
    private OrderController orderController;
    private PreOrderController preOrderController;
    private StaffController staffController;
    private ItemController itemController;
    private StockPurchaseController stockPurchaseController;

    public MainView(Shell shell, Composite contentArea, StackLayout stackLayout,
            CustomerView customerView, CustomerListView customerListView,
            OrderView orderView, OrderListView orderListView,
            PreOrderView preOrderView, PreOrderListView preOrderListView,
            StaffView staffView, StaffListView staffListView,
            ItemView itemView, ItemListView itemListView,
            ReportView reportView,
            StockPurchaseView stockPurchaseView, StockPurchaseListView stockPurchaseListView,
            TableManagementView tableManagementView,
            CustomerController customerController, OrderController orderController,
            PreOrderController preOrderController, StaffController staffController,
            ItemController itemController, StockPurchaseController stockPurchaseController,
            ReportController reportController,
            Staff loggedInStaff) {
        
    	 this.shell = shell;
         this.contentArea = contentArea;
         this.stackLayout = stackLayout;
         this.loggedInStaff = loggedInStaff;
        
        // Store view references
        this.customerView = customerView;
        this.customerListView = customerListView;
        this.orderView = orderView;
        this.orderListView = orderListView;
        this.preOrderView = preOrderView;
        this.preOrderListView = preOrderListView;
        this.staffView = staffView;
        this.staffListView = staffListView;
        this.itemView = itemView;
        this.itemListView = itemListView;
        this.stockPurchaseView = stockPurchaseView;
        this.stockPurchaseListView = stockPurchaseListView;
        this.reportView = reportView;
        this.reportView = reportView;  // Set the reportView field
        this.tableManagementView = tableManagementView;
        
        // Store controller references
        this.customerController = customerController;
        this.orderController = orderController;
        this.preOrderController = preOrderController;
        this.staffController = staffController;
        this.itemController = itemController;
        this.stockPurchaseController = stockPurchaseController;
        
        // Set up the shell with role in title
        shell.setText("Sales Management Sys - " + loggedInStaff.getJobRole() + " Dashboard");
        shell.setSize(1200, 800);
        
        // Create menu based on permissions
        createMenuBar();

        // Set default view - customer view for everyone
        stackLayout.topControl = customerView.getComposite();
        contentArea.layout();
    }
    
    private void createMenuBar() {
        Menu menuBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menuBar);
        
        String role = loggedInStaff.getJobRole();
        
        // --- CUSTOMERS MENU ---
        // All users can register customers
        if (AccessRightManager.hasPermission(role, AccessRightManager.REGISTER_CUSTOMER)) {
            MenuItem customersMenu = new MenuItem(menuBar, SWT.CASCADE);
            customersMenu.setText("&Customers");
            Menu customersSubMenu = new Menu(shell, SWT.DROP_DOWN);
            customersMenu.setMenu(customersSubMenu);

            MenuItem addCustomerItem = new MenuItem(customersSubMenu, SWT.PUSH);
            addCustomerItem.setText("&Add Customer");
            addCustomerItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    switchView(customerView.getComposite());
                }
            });
        
            MenuItem viewCustomersItem = new MenuItem(customersSubMenu, SWT.PUSH);
            viewCustomersItem.setText("&View Customers");
            viewCustomersItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    customerController.refreshCustomerList();
                    switchView(customerListView.getComposite());
                }
            });
        }
        
        // --- ORDERS MENU ---
        // All users can place orders
        if (AccessRightManager.hasPermission(role, AccessRightManager.PLACE_ORDER)) {
            MenuItem ordersMenu = new MenuItem(menuBar, SWT.CASCADE);
            ordersMenu.setText("&Orders");
            Menu ordersSubMenu = new Menu(shell, SWT.DROP_DOWN);
            ordersMenu.setMenu(ordersSubMenu);

            MenuItem addOrderItem = new MenuItem(ordersSubMenu, SWT.PUSH);
            addOrderItem.setText("&Add Order");
            addOrderItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    orderController.loadFormData();
                    switchView(orderView.getComposite());
                }
            });
        
            MenuItem viewOrdersItem = new MenuItem(ordersSubMenu, SWT.PUSH);
            viewOrdersItem.setText("&View Orders");
            viewOrdersItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    orderController.refreshOrderList();
                    switchView(orderListView.getComposite());
                }
            });
        }
        
        // --- PRE-ORDERS MENU ---
        // All users can place pre-orders
        if (AccessRightManager.hasPermission(role, AccessRightManager.PLACE_PREORDER)) {
            MenuItem preOrdersMenu = new MenuItem(menuBar, SWT.CASCADE);
            preOrdersMenu.setText("&Pre-Orders");
            Menu preOrdersSubMenu = new Menu(shell, SWT.DROP_DOWN);
            preOrdersMenu.setMenu(preOrdersSubMenu);

            MenuItem addPreOrderItem = new MenuItem(preOrdersSubMenu, SWT.PUSH);
            addPreOrderItem.setText("&Create Pre-Order");
            addPreOrderItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    preOrderController.loadFormData();
                    switchView(preOrderView.getComposite());
                }
            });
        
            MenuItem viewPreOrdersItem = new MenuItem(preOrdersSubMenu, SWT.PUSH);
            viewPreOrdersItem.setText("&View Pre-Orders");
            viewPreOrdersItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    preOrderController.refreshPreOrderList();
                    switchView(preOrderListView.getComposite());
                }
            });
        }
        
        // --- STAFF MENU ---
        // Only admins and managers can register staff
        if (AccessRightManager.hasPermission(role, AccessRightManager.REGISTER_STAFF)) {
            MenuItem staffMenu = new MenuItem(menuBar, SWT.CASCADE);
            staffMenu.setText("&Staff");
            Menu staffSubMenu = new Menu(shell, SWT.DROP_DOWN);
            staffMenu.setMenu(staffSubMenu);

            MenuItem addStaffItem = new MenuItem(staffSubMenu, SWT.PUSH);
            addStaffItem.setText("&Add Staff");
            addStaffItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    switchView(staffView.getComposite());
                }
            });
        
            MenuItem viewStaffItem = new MenuItem(staffSubMenu, SWT.PUSH);
            viewStaffItem.setText("&View Staff");
            viewStaffItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    staffController.refreshStaffList();
                    switchView(staffListView.getComposite());
                }
            });
        }
        
        // --- ITEMS MENU ---
        // Only admins, managers and sales officers can manage items
        if (AccessRightManager.hasPermission(role, AccessRightManager.MANAGE_ITEMS)) {
            MenuItem itemsMenu = new MenuItem(menuBar, SWT.CASCADE);
            itemsMenu.setText("&Items");
            Menu itemsSubMenu = new Menu(shell, SWT.DROP_DOWN);
            itemsMenu.setMenu(itemsSubMenu);

            MenuItem addItemItem = new MenuItem(itemsSubMenu, SWT.PUSH);
            addItemItem.setText("&Add Item");
            addItemItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    switchView(itemView.getComposite());
                }
            });
        
            MenuItem viewItemsItem = new MenuItem(itemsSubMenu, SWT.PUSH);
            viewItemsItem.setText("&View Items");
            viewItemsItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    itemController.refreshItemList();
                    switchView(itemListView.getComposite());
                }
            });
        }
        
        // --- STOCK MENU ---
        // Only admins and managers can manage stock
        if (AccessRightManager.hasPermission(role, AccessRightManager.MANAGE_STOCK)) {
            MenuItem stockMenu = new MenuItem(menuBar, SWT.CASCADE);
            stockMenu.setText("&Stock");
            Menu stockSubMenu = new Menu(shell, SWT.DROP_DOWN);
            stockMenu.setMenu(stockSubMenu);

            MenuItem recordStockItem = new MenuItem(stockSubMenu, SWT.PUSH);
            recordStockItem.setText("&Record Stock Purchase");
            recordStockItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    switchView(stockPurchaseView.getComposite());
                }
            });
        
            MenuItem viewStockItem = new MenuItem(stockSubMenu, SWT.PUSH);
            viewStockItem.setText("&View Stock Purchases");
            viewStockItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    stockPurchaseController.refreshStockPurchaseList();
                    switchView(stockPurchaseListView.getComposite());
                }
            });            
            
        }        
    

     // --- TABLES MENU ---
     // Only admins and managers can manage tables
     if (AccessRightManager.hasPermission(role, AccessRightManager.MANAGE_STOCK)) {
         MenuItem tablesMenu = new MenuItem(menuBar, SWT.CASCADE);
         tablesMenu.setText("&Tables");
         Menu tablesSubMenu = new Menu(shell, SWT.DROP_DOWN);
         tablesMenu.setMenu(tablesSubMenu);

         MenuItem manageTablesItem = new MenuItem(tablesSubMenu, SWT.PUSH);
         manageTablesItem.setText("&Manage Tables");
         manageTablesItem.addSelectionListener(new SelectionAdapter() {
             @Override
             public void widgetSelected(SelectionEvent e) {
                 switchView(tableManagementView.getComposite());
             }
         });
     }


        // --- REPORTS MENU ---
        // Only admins, managers and sales officers can view reports
        if (AccessRightManager.hasPermission(role, AccessRightManager.VIEW_REPORTS)) {
            MenuItem reportsMenu = new MenuItem(menuBar, SWT.CASCADE);
            reportsMenu.setText("&Reports");
            Menu reportsSubMenu = new Menu(shell, SWT.DROP_DOWN);
            reportsMenu.setMenu(reportsSubMenu);

            MenuItem generateReportsItem = new MenuItem(reportsSubMenu, SWT.PUSH);
            generateReportsItem.setText("&Generate Reports");
            generateReportsItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    switchView(reportView.getComposite());
                }
            });
        }
        
     // Add to the createMenuBar() method, perhaps at the bottom before Exit

     // --- SETTINGS MENU ---
     // Only Admin can change DB settings
     if (loggedInStaff.getJobRole().equals("Admin")) {
         MenuItem settingsMenu = new MenuItem(menuBar, SWT.CASCADE);
         settingsMenu.setText("&Settings");
         Menu settingsSubMenu = new Menu(shell, SWT.DROP_DOWN);
         settingsMenu.setMenu(settingsSubMenu);

         MenuItem dbConfigItem = new MenuItem(settingsSubMenu, SWT.PUSH);
         dbConfigItem.setText("&Database Configuration");
         dbConfigItem.addSelectionListener(new SelectionAdapter() {
             @Override
             public void widgetSelected(SelectionEvent e) {
                 // Create the config view on demand
                 DatabaseConfigView configView = new DatabaseConfigView(contentArea);
                 switchView(configView.getComposite());
             }
         });
     }
        
        // --- EXIT MENU ---
        // Exit is available to all users
        new MenuItem(menuBar, SWT.SEPARATOR);

        MenuItem exitItem = new MenuItem(menuBar, SWT.PUSH);
        exitItem.setText("&Exit");
        exitItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.close();
            }
        });
    }
    
    // Method to switch between views
    private void switchView(Composite viewToShow) {
        if (stackLayout.topControl != viewToShow) {
            stackLayout.topControl = viewToShow;
            contentArea.layout();
        }
    }

    public void open() {
        // Center the shell
        Monitor primary = shell.getDisplay().getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Point size = shell.getSize();
        int x = bounds.x + (bounds.width - size.x) / 2;
        int y = bounds.y + (bounds.height - size.y) / 2;
        shell.setLocation(x, y);
        
        // Force update of layout
        shell.layout(true, true);
        shell.open();
    }

    public Shell getShell() {
        return shell;
    }
}
