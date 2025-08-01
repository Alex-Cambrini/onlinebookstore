package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.Book;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;

public class AddBookServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddBookServlet.class.getName());
    static BookService bookService = new BookServiceImpl();

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        if (!StoreUtil.isLoggedIn(UserRole.SELLER, req.getSession())) {
            RequestDispatcher rd = req.getRequestDispatcher("SellerLogin.html");
            rd.include(req, res);
            pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
            return;
        }

        String bName = req.getParameter(BooksDBConstants.COLUMN_NAME);
        RequestDispatcher rd = req.getRequestDispatcher("SellerHome.html");
        rd.include(req, res);
        StoreUtil.setActiveTab(pw, "addbook");
        pw.println("<div class='container my-2'>");

        if (bName == null || bName.trim().isEmpty()) {
            showAddBookForm(pw);
            return;
        }

        String priceStr = req.getParameter(BooksDBConstants.COLUMN_PRICE);
        String qtyStr = req.getParameter(BooksDBConstants.COLUMN_QUANTITY);
        if (priceStr == null || !priceStr.matches("\\d+(\\.\\d+)?")) {
            pw.println("<table class=\"tab\"><tr><td>Invalid price format!</td></tr></table>");
            showAddBookForm(pw);
            return;
        }
        if (qtyStr == null || !qtyStr.matches("\\d+")) {
            pw.println("<table class=\"tab\"><tr><td>Invalid quantity format!</td></tr></table>");
            showAddBookForm(pw);
            return;
        }

        try {
            String uniqueID = UUID.randomUUID().toString();
            String bCode = uniqueID;
            String bAuthor = req.getParameter(BooksDBConstants.COLUMN_AUTHOR);
            double bPrice = Double.parseDouble(priceStr);
            int bQty = Integer.parseInt(qtyStr);

            Book book = new Book(bCode, bName, bAuthor, bPrice, bQty);
            String message = bookService.addBook(book);
            if ("SUCCESS".equalsIgnoreCase(message)) {
                pw.println(
                        "<table class=\"tab\"><tr><td>Book Detail Updated Successfully!<br/>Add More Books</td></tr></table>");
            } else {
                pw.println("<table class=\"tab\"><tr><td>Failed to Add Books! Fill up Carefully</td></tr></table>");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to add books", e);
            pw.println("<table class=\"tab\"><tr><td>Failed to Add Books! Fill up Carefully</td></tr></table>");
        }
    }

    private static void showAddBookForm(PrintWriter pw) {
        String form = "<table class=\"tab my-5\" style=\"width:40%;\">\r\n"
                + "        <tr>\r\n"
                + "            <td>\r\n"
                + "                <form action=\"addbook\" method=\"post\">\r\n"
                + "                    <!-- <label for=\"bookCode\">Book Code : </label><input type=\"text\" name=\"barcode\" id=\"bookCode\" placeholder=\"Enter Book Code\" required><br/> -->\r\n"
                + "                    <label for=\"bookName\">Book Name : </label> <input type=\"text\" name=\"name\" id=\"bookName\" placeholder=\"Enter Book's name\" required><br/>\r\n"
                + "                    <label for=\"bookAuthor\">Book Author : </label><input type=\"text\" name=\"author\" id=\"bookAuthor\" placeholder=\"Enter Author's Name\" required><br/>\r\n"
                + "                    <label for=\"bookPrice\">Book Price : </label><input type=\"number\" name=\"price\" placeholder=\"Enter the Price\" required><br/>\r\n"
                + "                    <label for=\"bookQuantity\">Book Qnty : </label><input type=\"number\" name=\"quantity\" id=\"bookQuantity\" placeholder=\"Enter the quantity\" required><br/>\r\n"
                + "                    <input class=\"btn btn-success my-2\" type=\"submit\" value=\" Add Book \">\r\n"
                + "                </form>\r\n"
                + "            </td>\r\n"
                + "        </tr>  \r\n"
                + "        <!-- <tr>\r\n"
                + "            <td><a href=\"index.html\">Go Back To Home Page</a></td>\r\n"
                + "        </tr> -->\r\n"
                + "    </table>";
        pw.println(form);
    }
}
