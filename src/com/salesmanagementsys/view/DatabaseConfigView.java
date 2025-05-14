package com.salesmanagementsys.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.util.Properties;

public class DatabaseConfigView {
    private Composite composite;
    private Text urlText;
    private Text userText;
    private Text passwordText;
    
    public DatabaseConfigView(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        
        createControls();
        loadCurrentConfig();
    }
    
    private File getConfigFile() {
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, ".salesmanagementsys");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        return new File(configDir, "config.properties");
    }
    
    private void createControls() {
        // URL field
        Label urlLabel = new Label(composite, SWT.NONE);
        urlLabel.setText("Database URL:");
        
        urlText = new Text(composite, SWT.BORDER);
        urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        // Username field
        Label userLabel = new Label(composite, SWT.NONE);
        userLabel.setText("Database User:");
        
        userText = new Text(composite, SWT.BORDER);
        userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        // Password field
        Label passwordLabel = new Label(composite, SWT.NONE);
        passwordLabel.setText("Database Password:");
        
        passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        // Save button
        Button saveButton = new Button(composite, SWT.PUSH);
        saveButton.setText("Save Configuration");
        GridData saveButtonData = new GridData(GridData.END, GridData.CENTER, false, false, 2, 1);
        saveButton.setLayoutData(saveButtonData);
        
        saveButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveConfiguration();
            }
        });
        
        // Note about restart
        Label noteLabel = new Label(composite, SWT.WRAP);
        noteLabel.setText("Note: Changes will take effect after restarting the application.");
        GridData noteData = new GridData(GridData.FILL_HORIZONTAL);
        noteData.horizontalSpan = 2;
        noteLabel.setLayoutData(noteData);
    }
    
    private void loadCurrentConfig() {
        try {
            File configFile = getConfigFile();
            // If the user config doesn't exist yet but the app config does, copy from there first
            if (!configFile.exists()) {
                File appConfigFile = new File("config.properties");
                if (appConfigFile.exists()) {
                    // Copy default config to user directory
                    try (FileInputStream fis = new FileInputStream(appConfigFile);
                         FileOutputStream fos = new FileOutputStream(configFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }
            
            if (configFile.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                    
                    urlText.setText(props.getProperty("db.url", ""));
                    userText.setText(props.getProperty("db.user", ""));
                    passwordText.setText(props.getProperty("db.password", ""));
                }
            }
        } catch (IOException e) {
            MessageBox messageBox = new MessageBox(composite.getShell(), SWT.ICON_ERROR | SWT.OK);
            messageBox.setText("Error");
            messageBox.setMessage("Failed to load configuration: " + e.getMessage());
            messageBox.open();
        }
    }

    private void saveConfiguration() {
        try {
            File configFile = getConfigFile();
            Properties props = new Properties();
            
            props.setProperty("db.url", urlText.getText().trim());
            props.setProperty("db.user", userText.getText().trim());
            props.setProperty("db.password", passwordText.getText());
            
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.store(fos, "Database Configuration");
                
                MessageBox messageBox = new MessageBox(composite.getShell(), SWT.ICON_INFORMATION | SWT.OK);
                messageBox.setText("Success");
                messageBox.setMessage("Configuration saved successfully. Please restart the application for changes to take effect.");
                messageBox.open();
            }
        } catch (IOException e) {
            MessageBox messageBox = new MessageBox(composite.getShell(), SWT.ICON_ERROR | SWT.OK);
            messageBox.setText("Error");
            messageBox.setMessage("Failed to save configuration: " + e.getMessage());
            messageBox.open();
        }
    }
    
    public Composite getComposite() {
        return composite;
    }
}

