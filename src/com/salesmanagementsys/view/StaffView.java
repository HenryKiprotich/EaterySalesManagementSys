package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import com.salesmanagementsys.controller.StaffController;
import com.salesmanagementsys.model.JobRole;
import org.eclipse.swt.graphics.*;
import java.util.List;

public class StaffView {
    private Composite composite;
    private Text firstNameText, lastNameText, hireDateText, usernameText, passwordText;
    private Combo jobRoleCombo;
    private List<JobRole> availableRoles;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;
    private static final int FIELD_WIDTH = 200;

    public StaffView(Composite parent, StaffController controller) {
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Personal Information Group
        Group personalGroup = new Group(composite, SWT.NONE);
        personalGroup.setText("Personal Information");
        personalGroup.setLayout(new GridLayout(2, false));
        GridData personalData = new GridData(SWT.FILL, SWT.TOP, true, false);
        personalData.horizontalSpan = 2;
        personalGroup.setLayoutData(personalData);
        personalGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        new Label(personalGroup, SWT.RIGHT).setText("First Name:");
        firstNameText = new Text(personalGroup, SWT.BORDER);
        GridData firstNameData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        firstNameData.widthHint = FIELD_WIDTH;
        firstNameText.setLayoutData(firstNameData);
        firstNameText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        new Label(personalGroup, SWT.RIGHT).setText("Last Name:");
        lastNameText = new Text(personalGroup, SWT.BORDER);
        GridData lastNameData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        lastNameData.widthHint = FIELD_WIDTH;
        lastNameText.setLayoutData(lastNameData);
        lastNameText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        new Label(personalGroup, SWT.RIGHT).setText("Hire Date (YYYY-MM-DD):");
        hireDateText = new Text(personalGroup, SWT.BORDER);
        GridData hireDateData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        hireDateData.widthHint = FIELD_WIDTH;
        hireDateText.setLayoutData(hireDateData);
        hireDateText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        new Label(personalGroup, SWT.RIGHT).setText("Job Role:");
        jobRoleCombo = new Combo(personalGroup, SWT.READ_ONLY);
        GridData jobRoleData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        jobRoleData.widthHint = FIELD_WIDTH;
        jobRoleCombo.setLayoutData(jobRoleData);
        jobRoleCombo.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        // Load job roles from database (will be populated by controller)
        loadJobRoles();
        
        // Account Information Group
        Group accountGroup = new Group(composite, SWT.NONE);
        accountGroup.setText("Account Information");
        accountGroup.setLayout(new GridLayout(2, false));
        GridData accountData = new GridData(SWT.FILL, SWT.TOP, true, false);
        accountData.horizontalSpan = 2;
        accountGroup.setLayoutData(accountData);
        accountGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        new Label(accountGroup, SWT.RIGHT).setText("Username:");
        usernameText = new Text(accountGroup, SWT.BORDER);
        GridData usernameData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        usernameData.widthHint = FIELD_WIDTH;
        usernameText.setLayoutData(usernameData);
        usernameText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        new Label(accountGroup, SWT.RIGHT).setText("Password:");
        passwordText = new Text(accountGroup, SWT.BORDER | SWT.PASSWORD);
        GridData passwordData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        passwordData.widthHint = FIELD_WIDTH;
        passwordText.setLayoutData(passwordData);
        passwordText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        // Button area
        Composite buttonComp = new Composite(composite, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
        buttonComp.setLayout(new GridLayout(1, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        Button addButton = new Button(buttonComp, SWT.PUSH);
        addButton.setText("Add Staff");
        GridData buttonData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        buttonData.widthHint = 120;
        addButton.setLayoutData(buttonData);
        addButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        addButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        addButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                controller.addStaff(
                    firstNameText.getText(),
                    lastNameText.getText(),
                    hireDateText.getText(),
                    jobRoleCombo.getText(),
                    usernameText.getText(),
                    passwordText.getText()
                );
            }
        });
    }

    public Composite getComposite() {
        return composite;
    }
    
    public void setAvailableRoles(List<JobRole> roles) {
        this.availableRoles = roles;
        loadJobRoles();
    }
    
    private void loadJobRoles() {
        if (availableRoles != null && !availableRoles.isEmpty()) {
            jobRoleCombo.removeAll();
            for (JobRole role : availableRoles) {
                jobRoleCombo.add(role.getName());
            }
            jobRoleCombo.select(0); // Select first role by default
        }
    }

    public void clearFields() {
        firstNameText.setText("");
        lastNameText.setText("");
        hireDateText.setText("");
        jobRoleCombo.deselectAll();
        usernameText.setText("");
        passwordText.setText("");
    }

    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}