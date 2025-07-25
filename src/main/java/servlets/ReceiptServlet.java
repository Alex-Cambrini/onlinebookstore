package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.model.Book;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;

public class ReceiptServlet extends HttpServlet {
    private final BookService bookService = new BookServiceImpl();
    private static final Logger LOGGER = Logger.getLogger(ReceiptServlet.class.getName());
    private static final String TD_CLOSE = "</td>";

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        if (!StoreUtil.isLoggedIn(UserRole.CUSTOMER, req.getSession())) {
            RequestDispatcher rd = req.getRequestDispatcher("CustomerLogin.html");
            rd.include(req, res);
            pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
            return;
        }

        try {
            List<Book> books = bookService.getAllBooks();
            int i = 0;
            RequestDispatcher rd = req.getRequestDispatcher("CustomerHome.html");
            rd.include(req, res);
            StoreUtil.setActiveTab(pw, "cart");

            pw.println("<div class=\"tab\">Your order status is as below</div>");
            pw.println(
                    "<div class=\"tab\">\r\n" +
                            "    <table>\r\n" +
                            "        <tr>\r\n" +
                            "            <th>Book Code</th>\r\n" +
                            "            <th>Book Name</th>\r\n" +
                            "            <th>Book Author</th>\r\n" +
                            "            <th>Book Price</th>\r\n" +
                            "            <th>Quantity</th>\r\n" +
                            "            <th>Amount</th>\r\n" +
                            "        </tr>");

            double total = 0.0;
            for (Book book : books) {
                i++;
                int quantity = parseQuantity(req, i);
                double amount = processBook(req, pw, book, quantity, i);
                if (amount > 0) {
                    total += amount;
                }
            }

            pw.println("</table><br/><div class='tab'>Total Paid Amount: " + total + "</div>");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ReceiptServlet service: {0}", e.getMessage());
        }
    }

    private double processBook(HttpServletRequest req, PrintWriter pw, Book book, int quantity, int index) {
        try {
            String checkParam = "checked" + index;
            String getChecked = req.getParameter(checkParam);

            if (book.getQuantity() < quantity) {
                pw.println(
                        "</table><div class=\"tab\" style='color:red;'>Please Select the Qty less than Available Books Quantity</div>");
                return -1;
            }

            if ("pay".equals(getChecked)) {
                pw.println("<tr><td>" + book.getBarcode() + TD_CLOSE);
                pw.println("<td>" + book.getName() + TD_CLOSE);
                pw.println("<td>" + book.getAuthor() + TD_CLOSE);
                pw.println("<td>" + book.getPrice() + TD_CLOSE);
                pw.println("<td>" + quantity + TD_CLOSE);
                double amount = book.getPrice() * quantity;
                pw.println("<td>" + amount + "</td></tr>");

                int updatedQty = book.getQuantity() - quantity;
                LOGGER.log(Level.INFO, "Updated quantity for book {0}: {1}",
                        new Object[] { book.getBarcode(), updatedQty });
                bookService.updateBookQtyById(book.getBarcode(), updatedQty);

                return amount;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing book: {0} - {1}",
                    new Object[] { book.getBarcode(), e.getMessage() });
        }
        return 0;
    }

    private int parseQuantity(HttpServletRequest req, int index) {
        String param = "qty" + index;
        try {
            return Integer.parseInt(req.getParameter(param));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
