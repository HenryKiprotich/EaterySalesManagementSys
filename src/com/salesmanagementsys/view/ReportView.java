package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import com.salesmanagementsys.controller.ReportController;
import java.time.LocalDate;
import java.util.Map;

public class ReportView {
    private Composite composite;
    private ReportController controller;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Table profitabilityTable;
    private Table topItemsTable;
    private TabFolder tabFolder;
    
    public ReportView(Composite parent, ReportController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.verticalSpacing = 5;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        createDateSelectionArea();
        createTabFolder();
    }
    
    private void createDateSelectionArea() {
        Group dateGroup = new Group(composite, SWT.NONE);
        dateGroup.setText("Select Date Range");
        GridLayout layout = new GridLayout(5, false);
        dateGroup.setLayout(layout);
        dateGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        dateGroup.setBackground(composite.getBackground());
        
        Label startLabel = new Label(dateGroup, SWT.NONE);
        startLabel.setText("Start Date:");
        startLabel.setBackground(dateGroup.getBackground());
        
        startDatePicker = new DatePicker(dateGroup);
        // Default to 30 days ago
        startDatePicker.setDate(LocalDate.now().minusDays(30));
        
        Label endLabel = new Label(dateGroup, SWT.NONE);
        endLabel.setText("End Date:");
        endLabel.setBackground(dateGroup.getBackground());
        
        endDatePicker = new DatePicker(dateGroup);
        // Default to today
        endDatePicker.setDate(LocalDate.now());
        
        Button generateButton = new Button(dateGroup, SWT.PUSH);
        generateButton.setText("Generate Reports");
        generateButton.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        generateButton.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        generateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                generateReports();
            }
        });
    }
    
    private void createTabFolder() {
        tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        // Create the Profitability Report tab
        TabItem profitabilityTab = new TabItem(tabFolder, SWT.NONE);
        profitabilityTab.setText("Profitability Report");
        
        Composite profitabilityComp = new Composite(tabFolder, SWT.NONE);
        profitabilityComp.setLayout(new GridLayout(1, false));
        profitabilityComp.setBackground(composite.getBackground());
        
        profitabilityTable = new Table(profitabilityComp, SWT.BORDER | SWT.FULL_SELECTION);
        profitabilityTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        profitabilityTable.setHeaderVisible(true);
        profitabilityTable.setLinesVisible(true);
        
        String[] profitabilityColumns = {"Category", "Total Cost (Ksh)", "Total Sales (Ksh)", "Profit/Loss (Ksh)", "Profit Margin (Ksh)"};
        for (String column : profitabilityColumns) {
            TableColumn tableColumn = new TableColumn(profitabilityTable, SWT.NONE);
            tableColumn.setText(column);
            tableColumn.setWidth(120);
        }
        
        profitabilityTab.setControl(profitabilityComp);
        
        // Create the Top Items tab
        TabItem topItemsTab = new TabItem(tabFolder, SWT.NONE);
        topItemsTab.setText("Top Selling Items");
        
        Composite topItemsComp = new Composite(tabFolder, SWT.NONE);
        topItemsComp.setLayout(new GridLayout(1, false));
        topItemsComp.setBackground(composite.getBackground());
        
        topItemsTable = new Table(topItemsComp, SWT.BORDER | SWT.FULL_SELECTION);
        topItemsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        topItemsTable.setHeaderVisible(true);
        topItemsTable.setLinesVisible(true);
        
        String[] topItemsColumns = {"Rank", "Item Name", "Quantity Sold", "Total Sales ($)"};
        for (String column : topItemsColumns) {
            TableColumn tableColumn = new TableColumn(topItemsTable, SWT.NONE);
            tableColumn.setText(column);
            tableColumn.setWidth(120);
        }
        
        topItemsTab.setControl(topItemsComp);
    }
    
    private void generateReports() {
        try {
            LocalDate startDate = startDatePicker.getDate();
            LocalDate endDate = endDatePicker.getDate();
            
            if (endDate.isBefore(startDate)) {
                showMessage("End date must be after start date", SWT.ICON_ERROR);
                return;
            }
            
            // Generate and display the reports
            controller.generateProfitabilityReport(startDate, endDate, profitabilityTable);
            controller.generateTopSellingItemsReport(startDate, endDate, topItemsTable);
            
        } catch (Exception e) {
            showMessage("Error generating reports: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }
    
    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
    
    public Composite getComposite() {
        return composite;
    }
    
    // Custom DatePicker class to simplify date selection
    private class DatePicker extends Composite {
        private Combo yearCombo;
        private Combo monthCombo;
        private Combo dayCombo;
        
        public DatePicker(Composite parent) {
            super(parent, SWT.NONE);
            GridLayout layout = new GridLayout(3, false);
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layout.horizontalSpacing = 3;
            setLayout(layout);
            
            // Create year combo
            yearCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
            int currentYear = LocalDate.now().getYear();
            for (int year = currentYear - 5; year <= currentYear; year++) {
                yearCombo.add(String.valueOf(year));
            }
            yearCombo.select(5); // Default to current year
            
            // Create month combo
            monthCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
            String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            for (String month : months) {
                monthCombo.add(month);
            }
            monthCombo.select(LocalDate.now().getMonthValue() - 1);
            
            // Create day combo
            dayCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
            updateDaysInMonth();
            
            // listeners to update days when month/year changes
            SelectionListener listener = new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    updateDaysInMonth();
                }
            };
            monthCombo.addSelectionListener(listener);
            yearCombo.addSelectionListener(listener);
        }
        
        private void updateDaysInMonth() {
            int year = Integer.parseInt(yearCombo.getText());
            int month = Integer.parseInt(monthCombo.getText());
            int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
            
            int selectedDay = dayCombo.getSelectionIndex() + 1;
            dayCombo.removeAll();
            
            for (int day = 1; day <= daysInMonth; day++) {
                dayCombo.add(String.format("%02d", day));
            }
            
            // Try to keep the same day selected, or select last day if current selection is out of bounds
            if (selectedDay > 0 && selectedDay <= daysInMonth) {
                dayCombo.select(selectedDay - 1);
            } else {
                dayCombo.select(0);
            }
        }
        
        public LocalDate getDate() {
            int year = Integer.parseInt(yearCombo.getText());
            int month = Integer.parseInt(monthCombo.getText());
            int day = Integer.parseInt(dayCombo.getText());
            return LocalDate.of(year, month, day);
        }
        
        public void setDate(LocalDate date) {
            yearCombo.setText(String.valueOf(date.getYear()));
            monthCombo.setText(String.format("%02d", date.getMonthValue()));
            updateDaysInMonth();
            dayCombo.setText(String.format("%02d", date.getDayOfMonth()));
        }
    }
}

