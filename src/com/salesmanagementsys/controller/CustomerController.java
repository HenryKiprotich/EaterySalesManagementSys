package com.salesmanagementsys.controller;

import com.salesmanagementsys.model.Customer;
import com.salesmanagementsys.model.DatabaseManager;
import com.salesmanagementsys.view.CustomerView;
import com.salesmanagementsys.view.CustomerListView;
import org.eclipse.swt.SWT;
import java.util.ArrayList;
import java.util.List;

//CustomerController class
public class CustomerController {
    private CustomerView customerView;
    private CustomerListView customerListView;
    private DatabaseManager dbManager;

    public CustomerController(DatabaseManager dbManager) {        
        this.dbManager = dbManager;
    }    
 
    private OrderController orderController;
    private PreOrderController preOrderController;

    // setter methods
    public void setOrderController(OrderController controller) {
        this.orderController = controller;
    }

    public void setPreOrderController(PreOrderController controller) {
        this.preOrderController = controller;
    }
    
    public void setCustomerView(CustomerView view) {
        this.customerView = view;
    }
    
    public void setCustomerListView(CustomerListView view) {
        this.customerListView = view;
    }
    
    // method to navigate to CustomerView
    public void navigateToCustomerView() {
        // Make CustomerView visible and CustomerListView invisible
        if (customerView != null) {
            customerView.getComposite().setVisible(true);
            customerListView.getComposite().setVisible(false);
            // Force layout update
            customerView.getComposite().getParent().layout(true, true);
        }
    }

 // addCustomer method
    public void addCustomer(String firstName, String lastName, String city, String mobileNumber) {
        try {
            if (firstName.isEmpty() || lastName.isEmpty()) {
                throw new IllegalArgumentException("First name and last name are required.");
            }
            
            // Check if customer name already exists
            if (dbManager.customerNameExists(firstName, lastName)) {
                customerView.showMessage("A customer with this name already exists.", SWT.ICON_ERROR);
                return;
            }
            
            // Check if mobile number already exists (if provided)
            if (mobileNumber != null && !mobileNumber.isEmpty() && dbManager.customerMobileExists(mobileNumber)) {
                customerView.showMessage("This mobile number is already registered to another customer.", SWT.ICON_ERROR);
                return;
            }
            
            Customer customer = new Customer(0, firstName, lastName, city, mobileNumber, 0.0);
            dbManager.addCustomer(customer);
            customerView.clearFields();
            customerView.showMessage("Customer added successfully!", SWT.ICON_INFORMATION);
            
            // Notify other controllers to refresh their customer lists
            if (orderController != null) {
                orderController.loadFormData();
            }
            if (preOrderController != null) {
                preOrderController.loadFormData();
            }
        } catch (Exception e) {
            customerView.showMessage("Error adding customer: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }

    
    public List<Customer> getAllCustomers() {
        try {
            return dbManager.getAllCustomers();
        } catch (Exception e) {
            customerListView.showMessage("Error retrieving customers: " + e.getMessage(), SWT.ICON_ERROR);
            return new ArrayList<>();
        }
    }

    public void refreshCustomerList() {
        try {
            List<Customer> customers = dbManager.getAllCustomers();
            customerListView.displayCustomers(customers);
        } catch (Exception e) {
            customerListView.showMessage("Error retrieving customers: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }    
}
