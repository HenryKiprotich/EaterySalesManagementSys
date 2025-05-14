package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*; 
import com.salesmanagementsys.controller.LoginController;

public class LoginView {
    private Shell shell;
    private Text usernameText, passwordText;
    private LoginController controller;
    private static final int FIELD_WIDTH = 200;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;
    private Runnable onLoginSuccess;

    public LoginView(Display display, LoginController controller) {
        this.controller = controller;

        shell = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("Sales Management Sys - Login");
        shell.setSize(400, 300);

        shell.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        GridLayout shellLayout = new GridLayout(1, false);
        shellLayout.marginWidth = MARGIN;
        shellLayout.marginHeight = MARGIN;
        shellLayout.verticalSpacing = SPACING;
        shell.setLayout(shellLayout);

        Composite formComposite = new Composite(shell, SWT.NONE);
        GridData formData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        formComposite.setLayoutData(formData);
        GridLayout formLayout = new GridLayout(2, false);
        formLayout.horizontalSpacing = SPACING;
        formLayout.verticalSpacing = SPACING;
        formComposite.setLayout(formLayout);
        formComposite.setBackground(shell.getBackground());

        Label titleLabel = new Label(formComposite, SWT.CENTER);
        titleLabel.setText("Enter Details to Login");
        titleLabel.setFont(new Font(display, "Arial", 10, SWT.BOLD));
        titleLabel.setForeground(display.getSystemColor(SWT.COLOR_DARK_MAGENTA));
        GridData titleData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
        titleLabel.setLayoutData(titleData);

        Group loginGroup = new Group(formComposite, SWT.NONE);
        loginGroup.setText("Staff Login");
        GridLayout groupLayout = new GridLayout(2, false);
        groupLayout.horizontalSpacing = SPACING;
        groupLayout.verticalSpacing = SPACING;
        loginGroup.setLayout(groupLayout);
        GridData groupData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
        loginGroup.setLayoutData(groupData);
        loginGroup.setBackground(shell.getBackground());

        Label usernameLabel = new Label(loginGroup, SWT.RIGHT);
        usernameLabel.setText("Username:");
        usernameText = new Text(loginGroup, SWT.BORDER);
        GridData usernameData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        usernameData.widthHint = FIELD_WIDTH;
        usernameText.setLayoutData(usernameData);
        usernameText.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

        Label passwordLabel = new Label(loginGroup, SWT.RIGHT);
        passwordLabel.setText("Password:");
        passwordText = new Text(loginGroup, SWT.BORDER | SWT.PASSWORD);
        GridData passwordData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        passwordData.widthHint = FIELD_WIDTH;
        passwordText.setLayoutData(passwordData);
        passwordText.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

        Button loginButton = new Button(formComposite, SWT.PUSH);
        loginButton.setText("Login");
        GridData buttonData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
        buttonData.widthHint = 100;
        loginButton.setLayoutData(buttonData);
        loginButton.setBackground(display.getSystemColor(SWT.COLOR_DARK_CYAN));
        loginButton.setForeground(display.getSystemColor(SWT.COLOR_WHITE));

        loginButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                String username = usernameText.getText().trim();
                String password = passwordText.getText();
                if (controller.validateLogin(username, password)) {
                    shell.close();
                    if (onLoginSuccess != null) {
                        onLoginSuccess.run();
                    }
                } else {
                    showMessage("Invalid username or password.", SWT.ICON_ERROR);
                }
            }
        });

        shell.setLocation(
            (display.getBounds().width - shell.getSize().x) / 2,
            (display.getBounds().height - shell.getSize().y) / 2
        );
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    public Shell getShell() {
        return shell;
    }

    public void open() {
        shell.open();
    }

    private void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(shell, style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}