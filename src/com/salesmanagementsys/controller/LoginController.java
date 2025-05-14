package com.salesmanagementsys.controller;

import com.salesmanagementsys.model.Staff;

import com.salesmanagementsys.util.PasswordEncryptionUtil;
import com.salesmanagementsys.model.DatabaseManager;
import java.sql.SQLException;

public class LoginController {
    private DatabaseManager dbManager;
    private Staff loggedInStaff;
    
    public LoginController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    public boolean validateLogin(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return false;
        }
        
        try {
            // Get the staff with the given username
            Staff staff = dbManager.getStaffByUsername(username);
            if (staff != null) {
            	
                // Verify the password against the stored hash
                boolean verified = PasswordEncryptionUtil.verifyPassword(password, staff.getPassword());
                                
                if (verified) {
                    loggedInStaff = staff;
                    return true;
                }
            } else {
                System.out.println("No staff found with username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    
    public Staff getLoggedInStaff() {
        return loggedInStaff;
    }
    
    public boolean isLoggedIn() {
        return loggedInStaff != null;
    }
    
    public void logout() {
        loggedInStaff = null;
    }
}
