package com.salesmanagementsys.controller;

import com.salesmanagementsys.model.DatabaseManager;
import com.salesmanagementsys.model.Order;
import com.salesmanagementsys.model.PreOrder;
import com.salesmanagementsys.model.StockPurchase;
import com.salesmanagementsys.model.Item;
import com.salesmanagementsys.view.ReportView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportController {
    private ReportView view;
    private DatabaseManager dbManager;
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public ReportController(ReportView view, DatabaseManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
    }

    public void setView(ReportView view) {
        this.view = view;
    }

    public void generateProfitabilityReport(LocalDate startDate, LocalDate endDate, Table table) {
        try {
            // Clear previous results
            table.removeAll();

            String startDateStr = startDate.format(formatter);
            String endDateStr = endDate.format(formatter);

            // Get stock purchases in date range
            List<StockPurchase> stockPurchases = dbManager.getStockPurchasesByDateRange(startDateStr, endDateStr);

            // Get orders and preorders in date range
            List<Order> orders = dbManager.getOrdersByDateRange(startDateStr, endDateStr);
            List<PreOrder> preOrders = dbManager.getPreOrdersByDateRange(startDateStr, endDateStr);

            // Calculate total costs from stock purchases
            double totalStockCost = stockPurchases.stream()
                .mapToDouble(purchase -> purchase.getPrice() * purchase.getQuantity())
                .sum();

            // Calculate total sales from orders and preorders
            double totalOrderSales = orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

            double totalPreOrderSales = preOrders.stream()
                .mapToDouble(PreOrder::getTotalAmount)
                .sum();

            double totalSales = totalOrderSales + totalPreOrderSales;
            double profit = totalSales - totalStockCost;
            double profitMargin = totalSales > 0 ? (profit / totalSales) * 100 : 0;

            // Add overall summary row
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, "Overall");
            item.setText(1, String.format("%.2f", totalStockCost));
            item.setText(2, String.format("%.2f", totalSales));
            item.setText(3, String.format("%.2f", profit));
            item.setText(4, String.format("%.1f", profitMargin));

            // Add breakdown for direct orders
            TableItem orderItem = new TableItem(table, SWT.NONE);
            orderItem.setText(0, "Direct Orders");
            orderItem.setText(1, "-");  // No direct cost attribution
            orderItem.setText(2, String.format("%.2f", totalOrderSales));
            orderItem.setText(3, "-");  // Can't calculate profit without costs
            orderItem.setText(4, "-");  // Can't calculate margin without costs

            // Add breakdown for pre-orders
            TableItem preOrderItem = new TableItem(table, SWT.NONE);
            preOrderItem.setText(0, "Pre-Orders");
            preOrderItem.setText(1, "-");  // No direct cost attribution
            preOrderItem.setText(2, String.format("%.2f", totalPreOrderSales));
            preOrderItem.setText(3, "-");  // Can't calculate profit without costs
            preOrderItem.setText(4, "-");  // Can't calculate margin without costs

            // Add breakdown for stock purchases
            TableItem stockItem = new TableItem(table, SWT.NONE);
            stockItem.setText(0, "Stock Purchases");
            stockItem.setText(1, String.format("%.2f", totalStockCost));
            stockItem.setText(2, "-");  // No direct sales
            stockItem.setText(3, "-");  // No direct profit
            stockItem.setText(4, "-");  // No margin

            // Adjust column widths
            for (TableColumn column : table.getColumns()) {
                column.pack();
            }

        } catch (SQLException e) {
            if (view != null) {
                view.showMessage("Error generating profitability report: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }

    public void generateTopSellingItemsReport(LocalDate startDate, LocalDate endDate, Table table) {
        try {
            // Clear previous results
            table.removeAll();

            String startDateStr = startDate.format(formatter);
            String endDateStr = endDate.format(formatter);

            // Get all items - using specific Item type
            List<Item> items = dbManager.getAllItems();

            // Get orders and preorders in date range
            List<Order> ordersInRange = dbManager.getOrdersByDateRange(startDateStr, endDateStr);
            List<PreOrder> preOrdersInRange = dbManager.getPreOrdersByDateRange(startDateStr, endDateStr);

            // Create map to track sales data: [itemId] -> [quantity, revenue]
            Map<Integer, int[]> itemSalesMap = new HashMap<>();
            for (Item item : items) {
                itemSalesMap.put(item.getId(), new int[]{0, 0});
            }

            // Gather sales data from regular orders
            for (Order order : ordersInRange) {
                List<DatabaseManager.OrderItemDetail> orderItems = dbManager.getOrderItemsWithDetails(order.getId(), 0);
                for (DatabaseManager.OrderItemDetail detail : orderItems) {
                    int[] salesData = itemSalesMap.get(detail.getItemId());
                    if (salesData != null) {
                        salesData[0] += detail.getQuantity();
                        salesData[1] += (int)(detail.getQuantity() * detail.getPrice());
                    }
                }
            }

            // Gather sales data from preorders
            for (PreOrder preOrder : preOrdersInRange) {
                List<DatabaseManager.OrderItemDetail> orderItems = dbManager.getOrderItemsWithDetails(0, preOrder.getId());
                for (DatabaseManager.OrderItemDetail detail : orderItems) {
                    int[] salesData = itemSalesMap.get(detail.getItemId());
                    if (salesData != null) {
                        salesData[0] += detail.getQuantity();
                        salesData[1] += (int)(detail.getQuantity() * detail.getPrice());
                    }
                }
            }

            // Create list of items with their sales data
            List<Object[]> itemSalesList = new ArrayList<>();
            for (Item item : items) {
                int[] sales = itemSalesMap.get(item.getId());
                if (sales[0] > 0) { // Only include items with sales
                    itemSalesList.add(new Object[]{item, sales[0], sales[1]});
                }
            }

            // Sort by quantity sold (descending)
            itemSalesList.sort((a, b) -> Integer.compare((int)b[1], (int)a[1]));

            // Add to table
            int rank = 1;
            for (Object[] data : itemSalesList) {
                Item item = (Item)data[0];
                int quantity = (int)data[1];
                double revenue = item.getPrice() * quantity;

                TableItem tableItem = new TableItem(table, SWT.NONE);
                tableItem.setText(0, String.valueOf(rank++));
                tableItem.setText(1, item.getName());
                tableItem.setText(2, String.valueOf(quantity));
                tableItem.setText(3, String.format("%.2f", revenue));
            }

            // Adjust column widths
            for (TableColumn column : table.getColumns()) {
                column.pack();
            }

        } catch (SQLException e) {
            if (view != null) {
                view.showMessage("Error generating top selling items report: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }

    // Helper method to check if a date string is within a range
    private boolean isDateInRange(String dateStr, String startDate, String endDate) {
        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);
            return !date.isBefore(start) && !date.isAfter(end);
        } catch (Exception e) {
            return false;
        }
    }
}
