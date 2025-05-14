package com.salesmanagementsys.model;

import java.util.*;

public class AccessRightManager {
    // Define permissions as constants
    public static final String REGISTER_CUSTOMER = "REGISTER_CUSTOMER";
    public static final String PLACE_ORDER = "PLACE_ORDER";
    public static final String PLACE_PREORDER = "PLACE_PREORDER";
    public static final String REGISTER_STAFF = "REGISTER_STAFF";
    public static final String MANAGE_ITEMS = "MANAGE_ITEMS";
    public static final String MANAGE_STOCK = "MANAGE_STOCK";
    public static final String VIEW_REPORTS = "VIEW_REPORTS";
    
    private static final Map<String, Set<String>> rolePermissions = new HashMap<>();
    
    static {
        // Initialize permissions for each role
        
        // Non-admin roles (e.g., Cashier, Server)
        Set<String> basicPermissions = new HashSet<>();
        basicPermissions.add(REGISTER_CUSTOMER);
        basicPermissions.add(PLACE_ORDER);
        basicPermissions.add(PLACE_PREORDER);
        
        // Manager role
        Set<String> managerPermissions = new HashSet<>(basicPermissions);
        managerPermissions.add(REGISTER_STAFF);
        managerPermissions.add(MANAGE_ITEMS);
        managerPermissions.add(MANAGE_STOCK);
        managerPermissions.add(VIEW_REPORTS);
        
        // Sales Officer role
        Set<String> salesOfficerPermissions = new HashSet<>(basicPermissions);
        salesOfficerPermissions.add(VIEW_REPORTS);
        salesOfficerPermissions.add(MANAGE_ITEMS);
        
        // Admin role has all permissions
        Set<String> adminPermissions = new HashSet<>(managerPermissions);
        
        // Store permissions for each role
        rolePermissions.put("Cashier", new HashSet<>(basicPermissions));
        rolePermissions.put("Server", new HashSet<>(basicPermissions));
        rolePermissions.put("Admin", adminPermissions);
        rolePermissions.put("Manager", managerPermissions);
        rolePermissions.put("Sales Officer", salesOfficerPermissions);
    }
    
    public static boolean hasPermission(String role, String permission) {
        if (role == null || permission == null) {
            return false;
        }
        
        Set<String> permissions = rolePermissions.get(role);
        return permissions != null && permissions.contains(permission);
    }
}

