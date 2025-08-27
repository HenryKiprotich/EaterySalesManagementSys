package com.salesmanagementsys.view;

import org.eclipse.swt.*;


import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.SelectionAdapter;  
import org.eclipse.swt.events.SelectionEvent;    
import com.salesmanagementsys.controller.StaffController;
import com.salesmanagementsys.model.Staff;
import com.salesmanagementsys.model.JobRole;      
import java.util.List;

public class StaffListView {
    private Composite composite;
    private Table staffTable;
    private StaffController controller;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;
    private Button editButton;

    public StaffListView(Composite parent, StaffController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        createStaffListSection(parent);
        
        // Register this view with the controller and request initial data load
        controller.setListView(this);
        controller.refreshStaffList();
        
    }

    private void createStaffListSection(Composite parent) {
        Group listGroup = new Group(composite, SWT.NONE);
        listGroup.setText("Staff List");
        GridLayout listLayout = new GridLayout(1, false);
        listGroup.setLayout(listLayout);
        GridData listData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listData.heightHint = 400;
        listGroup.setLayoutData(listData);
        listGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
       
        // Create table for staff
        staffTable = new Table(listGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        staffTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        staffTable.setHeaderVisible(true);
        staffTable.setLinesVisible(true);

        // Note: Not displaying password for security reasons
        String[] titles = {"ID", "First Name", "Last Name", "Hire Date", "Job Role", "Username"};
        for (String title : titles) {
            TableColumn column = new TableColumn(staffTable, SWT.NONE);
            column.setText(title);
            column.setWidth(100);
        }

        Composite buttonComp = new Composite(listGroup, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonComp.setLayout(new GridLayout(2, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        // Edit button
        editButton = new Button(buttonComp, SWT.PUSH);
        editButton.setText("Edit Staff");
        GridData editData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        editData.widthHint = 100;
        editButton.setLayoutData(editData);
        editButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        editButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        editButton.setEnabled(false);
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (staffTable.getSelectionCount() > 0) {
                    int staffId = Integer.parseInt(staffTable.getSelection()[0].getText(0));
                    showEditStaffDialog(staffId);
                }
            }
        });        

        Button refreshButton = new Button(buttonComp, SWT.PUSH);
        refreshButton.setText("Refresh");
        GridData refreshData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        refreshData.widthHint = 100;
        refreshButton.setLayoutData(refreshData);
        refreshButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        refreshButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        refreshButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                controller.refreshStaffList();
            }
        });
        
        // selection listener to enable/disable edit button
        staffTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editButton.setEnabled(staffTable.getSelectionCount() > 0);
            }
        });
    }    

    public void displayStaff(List<Staff> staffList) {
        staffTable.removeAll();

        for (Staff staff : staffList) {
            TableItem item = new TableItem(staffTable, SWT.NONE);
            item.setText(0, String.valueOf(staff.getId()));
            item.setText(1, staff.getFirstName());
            item.setText(2, staff.getLastName());
            item.setText(3, staff.getHireDate());
            item.setText(4, staff.getJobRole());
            item.setText(5, staff.getUsername());
            // Password not displayed for security reasons
        }

        // Adjust column widths
        for (TableColumn column : staffTable.getColumns()) {
            column.pack();
        }
    }
    
 // method to display the edit dialog
    private void showEditStaffDialog(int staffId) {
        try {
            Staff staff = controller.getStaffById(staffId);
            if (staff == null) return;
            
            Shell dialog = new Shell(composite.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            dialog.setText("Edit Staff");
            dialog.setLayout(new GridLayout(2, false));
            
            // First name
            Label firstNameLabel = new Label(dialog, SWT.NONE);
            firstNameLabel.setText("First Name:");
            Text firstNameText = new Text(dialog, SWT.BORDER);
            firstNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            firstNameText.setText(staff.getFirstName());
            
            // Last name
            Label lastNameLabel = new Label(dialog, SWT.NONE);
            lastNameLabel.setText("Last Name:");
            Text lastNameText = new Text(dialog, SWT.BORDER);
            lastNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            lastNameText.setText(staff.getLastName());
            
            // Hire date
            Label hireDateLabel = new Label(dialog, SWT.NONE);
            hireDateLabel.setText("Hire Date (YYYY-MM-DD):");
            Text hireDateText = new Text(dialog, SWT.BORDER);
            hireDateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            hireDateText.setText(staff.getHireDate());
            
            // Job role
            Label jobRoleLabel = new Label(dialog, SWT.NONE);
            jobRoleLabel.setText("Job Role:");
            Combo jobRoleCombo = new Combo(dialog, SWT.DROP_DOWN | SWT.READ_ONLY);
            jobRoleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            try {
                List<JobRole> roles = controller.getJobRoles();
                for (JobRole role : roles) {
                    jobRoleCombo.add(role.getName());
                }
                jobRoleCombo.setText(staff.getJobRole());
            } catch (Exception e) {
                showMessage("Error loading job roles: " + e.getMessage(), SWT.ICON_ERROR);
            }
            
            // Username
            Label usernameLabel = new Label(dialog, SWT.NONE);
            usernameLabel.setText("Username:");
            Text usernameText = new Text(dialog, SWT.BORDER);
            usernameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            usernameText.setText(staff.getUsername());
            
            // New password (optional)
            Label passwordLabel = new Label(dialog, SWT.NONE);
            passwordLabel.setText("New Password (optional):");
            Text passwordText = new Text(dialog, SWT.BORDER | SWT.PASSWORD);
            passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            // Buttons container
            Composite buttonContainer = new Composite(dialog, SWT.NONE);
            buttonContainer.setLayout(new GridLayout(2, true));
            GridData btnData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
            buttonContainer.setLayoutData(btnData);
            
            // Save button
            Button saveButton = new Button(buttonContainer, SWT.PUSH);
            saveButton.setText("Save");
            GridData saveData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            saveData.widthHint = 100;
            saveButton.setLayoutData(saveData);
            saveButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String firstName = firstNameText.getText().trim();
                    String lastName = lastNameText.getText().trim();
                    String hireDate = hireDateText.getText().trim();
                    String jobRole = jobRoleCombo.getText();
                    String username = usernameText.getText().trim();
                    String password = passwordText.getText().trim();
                    
                    if (firstName.isEmpty() || lastName.isEmpty() || hireDate.isEmpty() || jobRole.isEmpty() || username.isEmpty()) {
                        showMessage("All fields except password are required", SWT.ICON_ERROR);
                        return;
                    }
                    
                    controller.updateStaff(staff.getId(), firstName, lastName, hireDate, jobRole, username, password);
                    dialog.close();
                }
            });
            
            // Cancel button
            Button cancelButton = new Button(buttonContainer, SWT.PUSH);
            cancelButton.setText("Cancel");
            GridData cancelData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            cancelData.widthHint = 100;
            cancelButton.setLayoutData(cancelData);
            cancelButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    dialog.close();
                }
            });
            
            dialog.pack();
            dialog.open();
            
        } catch (Exception e) {
            showMessage("Error loading staff details: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }          
            
            

    public Composite getComposite() {
        return composite;
    }

    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}

