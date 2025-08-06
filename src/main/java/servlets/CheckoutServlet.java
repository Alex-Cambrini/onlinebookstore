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
import javax.servlet.http.HttpSession;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.model.UserRole;
import com.bittercode.util.StoreUtil;

public class CheckoutServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CheckoutServlet.class.getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = null;

        try {
            pw = res.getWriter();
            res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

            HttpSession session = req.getSession();

            if (!StoreUtil.isLoggedIn(UserRole.CUSTOMER, session)) {
                RequestDispatcher rd = req.getRequestDispatcher("CustomerLogin.html");
                rd.include(req, res);
                pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
                return;
            }

            RequestDispatcher rd = req.getRequestDispatcher("payment.html");
            rd.include(req, res);

            StoreUtil.setActiveTab(pw, "cart");

            pw.println("Total Amount<span class=\"price\" style=\"color: black\"><b>&#8377; "
                    + session.getAttribute("amountToPay")
                    + "</b></span>");

            pw.println("<input type=\"submit\" value=\"Pay & Place Order\" class=\"btn\">"
                    + "</form>");

            pw.println("</div>\r\n"
                    + " </div>\r\n"
                    + " </div>\r\n"
                    + " </div>");
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e, () -> "IO error in CheckoutServlet: " + e.getMessage());
        } catch (ServletException e) {
            LOGGER.log(Level.INFO, e, () -> "Servlet error in CheckoutServlet: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e, () -> "Unexpected error in CheckoutServlet: " + e.getMessage());
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
