package com.bittercode.util;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.bittercode.model.UserRole;

public class StoreUtil {
    private StoreUtil() {
        // Utility class, prevent instantiation
    }

    private static final String ITEMS_KEY = "items";

    public static boolean isLoggedIn(UserRole role, HttpSession session) {
        return session.getAttribute(role.toString()) != null;
    }

    public static void setActiveTab(PrintWriter pw, String activeTab) {
        pw.println("<script>document.getElementById(activeTab).classList.remove(\"active\");activeTab=" + activeTab
                + "</script>");
        pw.println("<script>document.getElementById('" + activeTab + "').classList.add(\"active\");</script>");
    }

    public static void updateCartItems(HttpServletRequest req) {
        String selectedBookId = req.getParameter("selectedBookId");
        if (selectedBookId == null)
            return;

        HttpSession session = req.getSession();
        String items = (String) session.getAttribute(ITEMS_KEY);
        boolean addToCart = req.getParameter("addToCart") != null;

        int itemQty = getItemQuantity(session, selectedBookId);

        if (addToCart) {
            if (items == null || items.isEmpty()) {
                items = selectedBookId;
            } else if (!items.contains(selectedBookId)) {
                items += "," + selectedBookId;
            }
            itemQty++;
            session.setAttribute(ITEMS_KEY, items);
            session.setAttribute("qty_" + selectedBookId, itemQty);
        } else {
            if (itemQty > 1) {
                itemQty--;
                session.setAttribute("qty_" + selectedBookId, itemQty);
            } else {
                session.removeAttribute("qty_" + selectedBookId);
                items = removeItemFromItemsString(items, selectedBookId);
                session.setAttribute(ITEMS_KEY, items);
            }
        }
    }

    private static int getItemQuantity(HttpSession session, String bookId) {
        Object qtyObj = session.getAttribute("qty_" + bookId);
        if (qtyObj instanceof Integer) {
            return (Integer) qtyObj;
        }
        return 0;
    }

    private static String removeItemFromItemsString(String items, String bookId) {
        if (items == null || items.isEmpty())
            return "";

        String[] parts = items.split(",");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.equals(bookId)) {
                if (sb.length() > 0)
                    sb.append(",");
                sb.append(part);
            }
        }
        return sb.toString();
    }
}
