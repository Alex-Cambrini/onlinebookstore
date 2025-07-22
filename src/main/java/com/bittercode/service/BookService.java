package com.bittercode.service;

import java.util.List;

import com.bittercode.model.Book;
import com.bittercode.model.StoreException;

public interface BookService {

    Book getBookById(String bookId) throws StoreException;

    List<Book> getAllBooks() throws StoreException;

    List<Book> getBooksByCommaSeperatedBookIds(String commaSeperatedBookIds) throws StoreException;

    String deleteBookById(String bookId) throws StoreException;

    String addBook(Book book) throws StoreException;

    String updateBookQtyById(String bookId, int quantity) throws StoreException;

    String updateBook(Book book) throws StoreException;
}
