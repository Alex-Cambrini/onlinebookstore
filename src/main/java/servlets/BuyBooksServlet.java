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
    BookService bookService = new BookServiceImpl();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);
        try (PrintWriter pw = res.getWriter()) {
            if (!StoreUtil.isLoggedIn(UserRole.CUSTOMER, req.getSession())) {
                includePage(req, res, "CustomerLogin.html");
                pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
                return;
            }
            processBooks(pw, req, res);
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
}
