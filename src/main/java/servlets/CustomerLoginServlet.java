package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import com.bittercode.service.UserService;
import com.bittercode.service.impl.UserServiceImpl;

public class CustomerLoginServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CustomerLoginServlet.class.getName());
    private static UserService authService = new UserServiceImpl();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        try {
            PrintWriter pw = res.getWriter();
            String uName = req.getParameter(UsersDBConstants.COLUMN_USERNAME);
            String pWord = req.getParameter(UsersDBConstants.COLUMN_PASSWORD);

            User user = authService.login(UserRole.CUSTOMER, uName, pWord, req.getSession());

            if (user != null) {
                RequestDispatcher rd = req.getRequestDispatcher("CustomerHome.html");
                rd.forward(req, res);
                pw.println("    <div id=\"topmid\"><h1>Welcome to Online <br>Book Store</h1></div>\r\n"
                        + "    <br>\r\n"
                        + "    <table class=\"tab\">\r\n"
                        + "        <tr>\r\n"
                        + "            <td><p>Welcome " + user.getFirstName() + ", Happy Learning !!</p></td>\r\n"
                        + "        </tr>\r\n"
                        + "    </table>");
            } else {
                RequestDispatcher rd = req.getRequestDispatcher("CustomerLogin.html");
                rd.forward(req, res);
                pw.println("<table class=\"tab\"><tr><td>Incorrect UserName or PassWord</td></tr></table>");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during customer login process", e);
        }
    }
}