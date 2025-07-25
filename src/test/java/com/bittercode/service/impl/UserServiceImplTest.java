package com.bittercode.service.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import com.bittercode.util.DBUtil;

class UserServiceImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private HttpSession mockSession;
    private MockedStatic<DBUtil> dbUtilMock;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockSession = mock(HttpSession.class);

        dbUtilMock = Mockito.mockStatic(DBUtil.class);
        dbUtilMock.when(DBUtil::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        userService = new UserServiceImpl();
    }

    @AfterEach
    void tearDown() {
        dbUtilMock.close();
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("firstname")).thenReturn("Mario");
        when(mockResultSet.getString("lastname")).thenReturn("Rossi");
        when(mockResultSet.getString("phone")).thenReturn("1234567890");

        User user = userService.login(UserRole.CUSTOMER, "test@mail.com", "pass", mockSession);

        assertNotNull(user);
        assertEquals("Mario", user.getFirstName());
        verify(mockSession).setAttribute(UserRole.CUSTOMER.toString(), "test@mail.com");
    }

    @Test
    void testIsLoggedInTrue() {
        when(mockSession.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@mail.com");
        boolean result = userService.isLoggedIn(UserRole.SELLER, mockSession);
        assertTrue(result);
    }

    @Test
    void testIsLoggedInFalse() {
        when(mockSession.getAttribute(UserRole.CUSTOMER.toString())).thenReturn(null);
        boolean result = userService.isLoggedIn(UserRole.CUSTOMER, mockSession);
        assertFalse(result);
    }

    @Test
    void testLogout() {
        boolean result = userService.logout(mockSession);
        verify(mockSession).removeAttribute(UserRole.CUSTOMER.toString());
        verify(mockSession).removeAttribute(UserRole.SELLER.toString());
        verify(mockSession).invalidate();
        assertTrue(result);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        User user = new User();
        user.setEmailId("test@mail.com");
        user.setPassword("pass");
        user.setFirstName("Mario");
        user.setLastName("Rossi");
        user.setAddress("Via Roma");
        user.setPhone(1234567890L);

        when(mockStatement.executeUpdate()).thenReturn(1);

        String result = userService.register(UserRole.CUSTOMER, user);
        assertEquals("SUCCESS", result);
    }

    @Test
    void testRegisterDuplicate() throws Exception {
        User user = new User();
        user.setEmailId("test@mail.com");
        user.setPassword("pass");
        user.setFirstName("Mario");
        user.setLastName("Rossi");
        user.setAddress("Via Roma");
        user.setPhone(1234567890L);

        when(mockStatement.executeUpdate()).thenThrow(new SQLException("Duplicate entry"));

        String result = userService.register(UserRole.CUSTOMER, user);
        assertEquals("User already registered with this email !!", result);
    }
}
