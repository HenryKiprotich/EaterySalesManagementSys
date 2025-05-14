package com.salesmanagementsys.controller;

import com.salesmanagementsys.model.DatabaseManager;
import com.salesmanagementsys.util.PasswordEncryptionUtil;
import com.salesmanagementsys.model.Staff;
import com.salesmanagementsys.model.JobRole;
import com.salesmanagementsys.view.StaffView;
import com.salesmanagementsys.view.StaffListView;
import java.util.List;
import java.sql.SQLException;
import org.eclipse.swt.SWT;

public class StaffController {
    private StaffView view;
    private StaffListView listView;
    private DatabaseManager dbManager;
    
    public StaffController(StaffView view, DatabaseManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
        if (view != null) {
            loadJobRoles();
        }
    }
    
	public void setView(StaffView view) {
		this.view = view;
		if (view != null) {
			loadJobRoles();
		}
	}
    
    private void loadJobRoles() {
        try {
            List<JobRole> roles = dbManager.getAllJobRoles();
            view.setAvailableRoles(roles);
        } catch (SQLException e) {
            if (view != null) {
                view.showMessage("Error loading job roles: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }
    
    public void setListView(StaffListView listView) {
        this.listView = listView;
    }
    
    public void addStaff(String firstName, String lastName, String hireDate, String jobRole, String username, String password) {
        try {
            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                if (view != null) {
                    view.showMessage("All fields are required", SWT.ICON_ERROR);
                }
                return;
            }
            
            // Check if username already exists
            if (dbManager.staffUsernameExists(username)) {
                if (view != null) {
                    view.showMessage("Staff username already exists. Please choose a different username.", SWT.ICON_ERROR);
                }
                return;
            }
          
            String encryptedPassword = PasswordEncryptionUtil.encryptPassword(password);
            Staff staff = new Staff(0, firstName, lastName, hireDate, jobRole, username, encryptedPassword);
            
            dbManager.addStaff(staff);
            if (view != null) {
                view.showMessage("Staff created successfully", SWT.ICON_INFORMATION);
                view.clearFields();
            }
            refreshStaffList();
        } catch (Exception e) {
            e.printStackTrace();
            if (view != null) {
                view.showMessage("Error creating staff: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }
    
    public void refreshStaffList() {
        if (listView != null) {
            try {
                List<Staff> staffList = dbManager.getAllStaff();
                listView.displayStaff(staffList);
            } catch (SQLException e) {
                listView.showMessage("Error retrieving staff: " + e.getMessage(), org.eclipse.swt.SWT.ICON_ERROR);
            }
        }
    } 
    
    public Staff getStaffById(int staffId) throws SQLException {
        return dbManager.getStaffById(staffId);
    }
    
    public List<JobRole> getJobRoles() throws SQLException {
        return dbManager.getAllJobRoles();
    }
    
    public void updateStaff(int staffId, String firstName, String lastName, String hireDate, 
                          String jobRole, String username, String newPassword) {
        try {
            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || hireDate.isEmpty() || 
                jobRole.isEmpty() || username.isEmpty()) {
                if (listView != null) {
                    listView.showMessage("All fields except password are required", SWT.ICON_ERROR);
                }
                return;
            }
            
            // Check if username already exists and is not the current staff's username
            Staff currentStaff = dbManager.getStaffById(staffId);
            if (!currentStaff.getUsername().equals(username) && dbManager.staffUsernameExists(username)) {
                if (listView != null) {
                    listView.showMessage("Username already exists. Please choose a different username.", 
                                        SWT.ICON_ERROR);
                }
                return;
            }
            
            // Process password - only update if a new one is provided
            String password = currentStaff.getPassword();
            if (!newPassword.isEmpty()) {
                password = PasswordEncryptionUtil.encryptPassword(newPassword);
            }
            
            Staff staff = new Staff(staffId, firstName, lastName, hireDate, jobRole, username, password);
            
            dbManager.updateStaff(staff);
            if (listView != null) {
                listView.showMessage("Staff updated successfully", SWT.ICON_INFORMATION);
            }
            refreshStaffList();
        } catch (Exception e) {
            e.printStackTrace();
            if (listView != null) {
                listView.showMessage("Error updating staff: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }  
    
}


