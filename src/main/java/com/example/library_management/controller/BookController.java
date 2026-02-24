package com.example.library_management.controller;

import com.example.library_management.dto.BookRequest;
import com.example.library_management.model.Book;
import com.example.library_management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;

    // create book (provide authorId)
    @PostMapping
    public Book create(@RequestBody BookRequest request) {
        return bookService.createBook(request);
    }

    @GetMapping
    public List<Book> all() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/genre/{genre}")
    public List<Book> byGenre(@PathVariable String genre) {
        return bookService.getBooksByGenre(genre);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @RequestBody BookRequest request) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "Book deleted with id: " + id;
    }
}

