package servlets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.util.StoreUtil;

class AddBookServletTest {

    AddBookServlet servlet;
    HttpServletRequest req;
    HttpServletResponse res;
    HttpSession session;
    RequestDispatcher rd;
    StringWriter sw;
    PrintWriter pw;

    static Stream<Arguments> provideAddBookResults() {
        return Stream.of(
                Arguments.of("SUCCESS", "Book Detail Updated Successfully"),
                Arguments.of("FAILURE", "Failed to Add Books!"),
                Arguments.of(null, "Failed to Add Books!"));
    }

    @BeforeEach
    void setup() throws Exception {
        servlet = new AddBookServlet();

        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        rd = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        when(res.getWriter()).thenReturn(pw);

        AddBookServlet.bookService = mock(BookService.class);
    }

    @Test
    void testNotLoggedInRedirectsToLogin() throws Exception {
        try (MockedStatic<StoreUtil> storeUtilMock = mockStatic(StoreUtil.class)) {
            storeUtilMock.when(() -> StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(false);
            when(req.getRequestDispatcher("SellerLogin.html")).thenReturn(rd);

            servlet.service(req, res);

            verify(rd).include(req, res);
            assertTrue(sw.toString().contains("Please Login First to Continue!!"));
        }
    }

    @Test
    void testShowAddBookFormWhenNameNull() throws Exception {
        when(StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
        when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn(null);
        when(req.getRequestDispatcher("SellerHome.html")).thenReturn(rd);

        servlet.service(req, res);

        verify(rd).include(req, res);
        assertTrue(sw.toString().contains("<form action=\"addbook\" method=\"post\">"));
    }

    @Test
    void testAddBookInvalidPrice() throws Exception {
        when(StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
        when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Test Book");
        when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Author");
        when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("notanumber");
        when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("5");
        when(req.getRequestDispatcher("SellerHome.html")).thenReturn(rd);

        servlet.service(req, res);

        verify(rd).include(req, res);
        String output = sw.toString();
        assertTrue(output.contains("Invalid price format!"));
    }

    @Test
    void testAddBookServiceException() throws Exception {
        when(StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
        when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Test Book");
        when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Author");
        when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("10.0");
        when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("5");
        when(req.getRequestDispatcher("SellerHome.html")).thenReturn(rd);

        when(AddBookServlet.bookService.addBook(any())).thenReturn(null);

        servlet.service(req, res);

        verify(rd).include(req, res);
        String output = sw.toString();
        assertTrue(output.contains("Failed to Add Books!"));
    }

    @ParameterizedTest
    @MethodSource("provideAddBookResults")
    void testAddBookWithVariousResults(String serviceResult, String expectedOutput) throws Exception {
        when(StoreUtil.isLoggedIn(UserRole.SELLER, session)).thenReturn(true);
        when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Test Book");
        when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Author");
        when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("10");
        when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("5");
        when(req.getRequestDispatcher("SellerHome.html")).thenReturn(rd);
        when(AddBookServlet.bookService.addBook(any())).thenReturn(serviceResult);

        servlet.service(req, res);

        verify(rd).include(req, res);
        String output = sw.toString();
        assertTrue(output.contains(expectedOutput));
    }
}
