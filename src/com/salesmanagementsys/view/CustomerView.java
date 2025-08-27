package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import com.salesmanagementsys.controller.CustomerController;
import org.eclipse.swt.graphics.*;

public class CustomerView {
    private Composite composite;
    private Text firstNameText, lastNameText, cityText, mobileText;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;
    private static final int FIELD_WIDTH = 200;

    public CustomerView(Composite parent, CustomerController controller) {
        composite = new Composite(parent, SWT.NONE);
        
        // Add explicit GridData settings for main composite
        GridData compositeData = new GridData(SWT.FILL, SWT.FILL, true, true);
        composite.setLayoutData(compositeData);
        
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Customer Information Group
        Group customerGroup = new Group(composite, SWT.NONE);
        customerGroup.setText("Customer Information");
        GridLayout groupLayout = new GridLayout(2, false);
        groupLayout.horizontalSpacing = SPACING;
        groupLayout.verticalSpacing = SPACING;
        customerGroup.setLayout(groupLayout);
        GridData groupData = new GridData(SWT.FILL, SWT.FILL, true, true); // Changed to FILL, FILL
        groupData.horizontalSpan = 2;
        customerGroup.setLayoutData(groupData);
        customerGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));   
   

        // fields with fixed width
        Label firstNameLabel = new Label(customerGroup, SWT.RIGHT);
        firstNameLabel.setText("First Name:");
        firstNameText = new Text(customerGroup, SWT.BORDER);
        GridData firstNameData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        firstNameData.widthHint = FIELD_WIDTH;
        firstNameText.setLayoutData(firstNameData);
        firstNameText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        Label lastNameLabel = new Label(customerGroup, SWT.RIGHT);
        lastNameLabel.setText("Last Name:");
        lastNameText = new Text(customerGroup, SWT.BORDER);
        GridData lastNameData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        lastNameData.widthHint = FIELD_WIDTH;
        lastNameText.setLayoutData(lastNameData);
        lastNameText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        Label cityLabel = new Label(customerGroup, SWT.RIGHT);
        cityLabel.setText("City:");
        cityText = new Text(customerGroup, SWT.BORDER);
        GridData cityData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        cityData.widthHint = FIELD_WIDTH;
        cityText.setLayoutData(cityData);
        cityText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        Label mobileLabel = new Label(customerGroup, SWT.RIGHT);
        mobileLabel.setText("Mobile Number:");
        mobileText = new Text(customerGroup, SWT.BORDER);
        GridData mobileData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        mobileData.widthHint = FIELD_WIDTH;
        mobileText.setLayoutData(mobileData);
        mobileText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        // Button section with styling
        Composite buttonComp = new Composite(composite, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
        buttonComp.setLayout(new GridLayout(1, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        Button addButton = new Button(buttonComp, SWT.PUSH);
        addButton.setText("Add Customer");
        GridData buttonData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        buttonData.widthHint = 120;
        addButton.setLayoutData(buttonData);
        addButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        addButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        addButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                controller.addCustomer(
                    firstNameText.getText(),
                    lastNameText.getText(),
                    cityText.getText(),
                    mobileText.getText()
                );
            }
        });
    }

    public Composite getComposite() {
        return composite;
    }

    public void clearFields() {
        firstNameText.setText("");
        lastNameText.setText("");
        cityText.setText("");
        mobileText.setText("");
    }

    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}