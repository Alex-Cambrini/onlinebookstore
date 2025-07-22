package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

    BookService bookService = new BookServiceImpl();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        try (PrintWriter pw = res.getWriter()) {
            if (!StoreUtil.isLoggedIn(UserRole.CUSTOMER, req.getSession())) {
                handleLoginRequired(req, res, pw);
                return;
            }
            try {
                processBooks(pw, req, res);
            } catch (ServletException | IOException e) {
                sendInternalServerError(res);
            }
        } catch (IOException e) {
            sendInternalServerError(res);
        }
    }

    private void handleLoginRequired(HttpServletRequest req, HttpServletResponse res, PrintWriter pw) throws IOException {
        try {
            includePage(req, res, "CustomerLogin.html");
        } catch (ServletException | IOException e) {
            sendInternalServerError(res);
            return;
        }
        pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!" + TD_CLOSE + "</td></tr></table>");
    }

    private void processBooks(PrintWriter pw, HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        List<Book> books = bookService.getAllBooks();
        includePage(req, res, "CustomerHome.html");
        StoreUtil.setActiveTab(pw, "cart");
        pw.println("<div class=\"tab hd brown \">Books Available In Our Store</div>");
        pw.println("<div class=\"tab\"><form action=\"buys\" method=\"post\">");
        pw.println("<table>\r\n" +
                "			<tr>\r\n" +
                "				<th>Books</th>\r\n" +
                "				<th>Code</th>\r\n" +
                "				<th>Name</th>\r\n" +
                "				<th>Author</th>\r\n" +
                "				<th>Price</th>\r\n" +
                "				<th>Avail</th>\r\n" +
                "				<th>Qty</th>\r\n" +
                "			</tr>");
        int i = 0;
        for (Book book : books) {
            i++;
            String n = "checked" + i;
            String q = "qty" + i;
            pw.println("<tr>\r\n" +
                    "				<td>\r\n" +
                    "					<input type=\"checkbox\" name=\"" + n + "\" value=\"pay\">\r\n" +
                    "				</td>");
            pw.println("<td>" + book.getBarcode() + "</td>");
            pw.println("<td>" + book.getName() + "</td>");
            pw.println("<td>" + book.getAuthor() + "</td>");
            pw.println("<td>" + book.getPrice() + "</td>");
            pw.println("<td>" + book.getQuantity() + "</td>");
            pw.println("<td><input type=\"text\" name=\"" + q + "\" value=\"0\" style=\"text-align:center\"></td></tr>");
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
            System.err.println("Failed to send error response: " + e.getMessage());
        }
    }
}
