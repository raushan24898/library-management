package com.example.library_management.service;

import com.example.library_management.dto.BookRequest;
import com.example.library_management.model.Book;
import java.util.List;

public interface BookService {
    Book createBook(BookRequest request);
    Book getBookById(Long id);
    List<Book> getAllBooks();
    List<Book> getBooksByGenre(String genre);
    List<Book> getBooksByAuthorId(Long authorId);
    Book updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
}
