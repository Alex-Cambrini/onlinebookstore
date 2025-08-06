package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

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

public class BuyBooksServlet extends HttpServlet {
    private static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error";
    private static final String TD_CLOSE = "</td>";
    private static final Logger logger = Logger.getLogger(BuyBooksServlet.class.getName());

    private static final BookService bookService = new BookServiceImpl();


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        try (PrintWriter pw = res.getWriter()) {
            if (!StoreUtil.isLoggedIn(UserRole.CUSTOMER, req.getSession())) {
                handleLoginRequired(req, res, pw);
                return;
            }
            processBooksSafe(pw, req, res);

        } catch (IOException e) {
            logger.log(Level.INFO, e, () -> "Failed to get writer from response: " + e.getMessage());
            sendInternalServerError(res);
        }
    }

    private void handleLoginRequired(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) throws IOException {
        try {
            includePage(req, res, "CustomerLogin.html");
        } catch (ServletException e) {
            logger.log(Level.INFO, e, () -> "ServletException while including CustomerLogin.html: " + e.getMessage());
            sendInternalServerError(res);
            return;
        } catch (IOException e) {
            logger.log(Level.INFO, e, () -> "IOException while including CustomerLogin.html: " + e.getMessage());
            sendInternalServerError(res);
            return;
        }
        pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!" + TD_CLOSE + "</td></tr></table>");
    }

    private void processBooksSafe(PrintWriter pw, HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            processBooks(pw, req, res);
        } catch (ServletException e) {
            logger.log(Level.INFO, e, () -> "ServletException during book processing: " + e.getMessage());
            sendInternalServerError(res);
        } catch (IOException e) {
            logger.log(Level.INFO, e, () -> "IOException during book processing: " + e.getMessage());
            sendInternalServerError(res);
        }
    }

    private void processBooks(PrintWriter pw, HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        List<Book> books = bookService.getAllBooks();
        includePage(req, res, "CustomerHome.html");
        StoreUtil.setActiveTab(pw, "cart");
        pw.println("<div class=\"tab hd brown \">Books Available In Our Store</div>");
        pw.println("<div class=\"tab\"><form action=\"buys\" method=\"post\">");
        pw.println("<table>\r\n" +
                "           <tr>\r\n" +
                "               <th>Books</th>\r\n" +
                "               <th>Code</th>\r\n" +
                "               <th>Name</th>\r\n" +
                "               <th>Author</th>\r\n" +
                "               <th>Price</th>\r\n" +
                "               <th>Avail</th>\r\n" +
                "               <th>Qty</th>\r\n" +
                "           </tr>");
        int i = 0;
        for (Book book : books) {
            i++;
            String n = "checked" + i;
            String q = "qty" + i;
            pw.println("<tr>\r\n" +
                    "               <td>\r\n" +
                    "                   <input type=\"checkbox\" name=\"" + n + "\" value=\"pay\">\r\n" +
                    "               " + TD_CLOSE);
            pw.println("<td>" + book.getBarcode() + TD_CLOSE);
            pw.println("<td>" + book.getName() + TD_CLOSE);
            pw.println("<td>" + book.getAuthor() + TD_CLOSE);
            pw.println("<td>" + book.getPrice() + TD_CLOSE);
            pw.println("<td>" + book.getQuantity() + TD_CLOSE);
            pw.println("<td><input type=\"text\" name=\"" + q + "\" value=\"0\" style=\"text-align:center\">" + TD_CLOSE + "</tr>");
        }
        pw.println("</table>\r\n" +
                "<input type=\"submit\" value=\" PAY NOW \"><br/>" +
                "</form>\r\n" +
                "</div>");
    }

    private void includePage(HttpServletRequest req, HttpServletResponse res, String page)
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(page);
        rd.include(req, res);
    }

    private void sendInternalServerError(HttpServletResponse res) {
        try {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
        } catch (IOException e) {
            logger.log(Level.INFO, e, () -> "Failed to send error response: " + e.getMessage());
        }
    }
}
