package com.bittercode.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.bittercode.model.Book;
import com.bittercode.util.DBUtil;

class BookServiceImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBUtil> dbUtilMock;

    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        dbUtilMock = Mockito.mockStatic(DBUtil.class);
        dbUtilMock.when(DBUtil::getConnection).thenReturn(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() {
        dbUtilMock.close();
    }

    @Test
    void testGetAllBooksReturnsList() throws Exception {
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString(1)).thenReturn("001");
        when(mockResultSet.getString(2)).thenReturn("Book Title");
        when(mockResultSet.getString(3)).thenReturn("Author");
        when(mockResultSet.getInt(4)).thenReturn(100);
        when(mockResultSet.getInt(5)).thenReturn(10);

        BookServiceImpl service = new BookServiceImpl();
        List<Book> books = service.getAllBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        Book book = books.get(0);
        assertEquals("001", book.getBarcode());
        assertEquals("Book Title", book.getName());
        assertEquals("Author", book.getAuthor());
        assertEquals(100, book.getPrice());
        assertEquals(10, book.getQuantity());
    }
}
