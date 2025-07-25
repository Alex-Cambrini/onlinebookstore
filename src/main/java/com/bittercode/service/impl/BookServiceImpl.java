package com.bittercode.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.bittercode.constant.ResponseCode;
import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.Book;
import com.bittercode.model.StoreException;
import com.bittercode.service.BookService;
import com.bittercode.util.DBUtil;

public class BookServiceImpl implements BookService {

    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    private static final String WHERE_CLAUSE = " WHERE ";
    private static final Logger LOGGER = Logger.getLogger(BookServiceImpl.class.getName());
    private static final String GET_ALL_BOOKS_QUERY = SELECT_ALL_FROM + BooksDBConstants.TABLE_BOOK;
    private static final String GET_BOOK_BY_ID_QUERY = SELECT_ALL_FROM + BooksDBConstants.TABLE_BOOK
            + WHERE_CLAUSE + BooksDBConstants.COLUMN_BARCODE + " = ?";

    private static final String DELETE_BOOK_BY_ID_QUERY = "DELETE FROM " + BooksDBConstants.TABLE_BOOK + WHERE_CLAUSE
            + BooksDBConstants.COLUMN_BARCODE + "=?";

    private static final String ADD_BOOK_QUERY = "INSERT INTO " + BooksDBConstants.TABLE_BOOK + " VALUES(?,?,?,?,?)";

    private static final String UPDATE_BOOK_QTY_BY_ID_QUERY = "UPDATE " + BooksDBConstants.TABLE_BOOK + " SET "
            + BooksDBConstants.COLUMN_QUANTITY + "=? WHERE " + BooksDBConstants.COLUMN_BARCODE + "=?";

    private static final String UPDATE_BOOK_BY_ID_QUERY = "UPDATE " + BooksDBConstants.TABLE_BOOK + " SET "
            + BooksDBConstants.COLUMN_NAME + "=?, "
            + BooksDBConstants.COLUMN_AUTHOR + "=?, "
            + BooksDBConstants.COLUMN_PRICE + "=?, "
            + BooksDBConstants.COLUMN_QUANTITY + "=? "
            + " WHERE " + BooksDBConstants.COLUMN_BARCODE + "=?";

    @Override
    public Book getBookById(String bookId) throws StoreException {
        Book book = null;
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(GET_BOOK_BY_ID_QUERY)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    book = new Book(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4),
                            rs.getInt(5));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in getBookById: " + e.getMessage());
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return book;
    }

    @Override
    public List<Book> getAllBooks() throws StoreException {
        List<Book> books = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(GET_ALL_BOOKS_QUERY);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5)));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in getAllBooks: " + e.getMessage());
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return books;
    }

    @Override
    public String deleteBookById(String bookId) throws StoreException {
        String response = ResponseCode.FAILURE.name();
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(DELETE_BOOK_BY_ID_QUERY)) {
            ps.setString(1, bookId);
            int k = ps.executeUpdate();
            if (k == 1) {
                response = ResponseCode.SUCCESS.name();
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in deleteBookById: " + e.getMessage());
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return response;
    }

    @Override
    public String addBook(Book book) throws StoreException {
        String responseCode = ResponseCode.FAILURE.name();
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(ADD_BOOK_QUERY)) {
            ps.setString(1, book.getBarcode());
            ps.setString(2, book.getName());
            ps.setString(3, book.getAuthor());
            ps.setDouble(4, book.getPrice());
            ps.setInt(5, book.getQuantity());
            int k = ps.executeUpdate();
            if (k == 1) {
                responseCode = ResponseCode.SUCCESS.name();
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in addBook: " + e.getMessage());
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return responseCode;
    }

    @Override
    public String updateBookQtyById(String bookId, int quantity) throws StoreException {
        String responseCode = ResponseCode.FAILURE.name();
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(UPDATE_BOOK_QTY_BY_ID_QUERY)) {
            ps.setInt(1, quantity);
            ps.setString(2, bookId);
            int k = ps.executeUpdate();
            if (k == 1) {
                responseCode = ResponseCode.SUCCESS.name();
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in updateBookQtyById: " + e.getMessage());
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return responseCode;
    }

    @Override
    public List<Book> getBooksByCommaSeperatedBookIds(String commaSeparatedBookIds) throws StoreException {
        List<Book> books = new ArrayList<>();
        String[] ids = commaSeparatedBookIds.split(",");
        String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));

        String query = SELECT_ALL_FROM + BooksDBConstants.TABLE_BOOK +
                WHERE_CLAUSE + BooksDBConstants.COLUMN_BARCODE + " IN (" + placeholders + ")";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            for (int i = 0; i < ids.length; i++) {
                ps.setString(i + 1, ids[i].trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4),
                            rs.getInt(5)));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in getBooksByCommaSeperatedBookIds: " + e.getMessage());
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return books;
    }

    @Override
    public String updateBook(Book book) throws StoreException {
        String responseCode = ResponseCode.FAILURE.name();
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(UPDATE_BOOK_BY_ID_QUERY)) {
            ps.setString(1, book.getName());
            ps.setString(2, book.getAuthor());
            ps.setDouble(3, book.getPrice());
            ps.setInt(4, book.getQuantity());
            ps.setString(5, book.getBarcode());
            int k = ps.executeUpdate();
            if (k == 1) {
                responseCode = ResponseCode.SUCCESS.name();
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in updateBook: " + e.getMessage());
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return responseCode;
    }
}
