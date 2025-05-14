// Create new file: TableAssignmentDialog.java in com.amazingdessertbar.view package
package com.salesmanagementsys.view;

import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import com.salesmanagementsys.model.Table;
import java.util.List;

public class TableAssignmentDialog {
    private Shell dialog;
    private List<Table> availableTables;
    private Table selectedTable;
    
    public TableAssignmentDialog(Shell parent, List<Table> availableTables) {
        this.availableTables = availableTables;
        
        dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("Assign Table");
        dialog.setLayout(new GridLayout(1, false));
        
        // Create table list
        Label instructionLabel = new Label(dialog, SWT.NONE);
        instructionLabel.setText("Select a table to assign:");
        
        org.eclipse.swt.widgets.Table tableList = new org.eclipse.swt.widgets.Table(
            dialog, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
        tableList.setHeaderVisible(true);
        tableList.setLinesVisible(true);
        GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableData.heightHint = 200;
        tableData.widthHint = 400;
        tableList.setLayoutData(tableData);
        
        // Create columns
        String[] titles = {"Table Number", "Capacity", "Status"};
        for (String title : titles) {
            TableColumn column = new TableColumn(tableList, SWT.NONE);
            column.setText(title);
            column.setWidth(100);
        }
        
        // Fill table with data
        for (Table table : availableTables) {
            TableItem item = new TableItem(tableList, SWT.NONE);
            item.setText(0, table.getNumber());
            item.setText(1, String.valueOf(table.getCapacity()));
            item.setText(2, table.getStatus());
            item.setData(table);
        }
        
        // Adjust columns width
        for (TableColumn column : tableList.getColumns()) {
            column.pack();
        }
        
        // Create button composite
        Composite buttonComp = new Composite(dialog, SWT.NONE);
        buttonComp.setLayout(new GridLayout(2, true));
        buttonComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        
        // Assign button
        Button assignButton = new Button(buttonComp, SWT.PUSH);
        assignButton.setText("Assign");
        GridData assignData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        assignData.widthHint = 100;
        assignButton.setLayoutData(assignData);
        assignButton.setEnabled(false);
        
        // Cancel button
        Button cancelButton = new Button(buttonComp, SWT.PUSH);
        cancelButton.setText("Cancel");
        GridData cancelData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        cancelData.widthHint = 100;
        cancelButton.setLayoutData(cancelData);
        
        // Selection listener for table list
        tableList.addListener(SWT.Selection, event -> {
            TableItem[] items = tableList.getSelection();
            assignButton.setEnabled(items.length > 0);
        });
        
        // Assign button listener
        assignButton.addListener(SWT.Selection, event -> {
            TableItem[] items = tableList.getSelection();
            if (items.length > 0) {
                selectedTable = (Table) items[0].getData();
                dialog.close();
            }
        });
        
        // Cancel button listener
        cancelButton.addListener(SWT.Selection, event -> {
            selectedTable = null;
            dialog.close();
        });
        
        dialog.pack();
    }
    
    public Table open() {
        dialog.open();
        Display display = dialog.getDisplay();
        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return selectedTable;
    }
}
