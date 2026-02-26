package com.example.library_management.controller;

import com.example.library_management.model.Author;
import com.example.library_management.service.AuthorService;
import com.example.library_management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService; // to fetch books by author

    @PostMapping
    public Author createAuthor(@RequestBody Author author) {
        return authorService.createAuthor(author);
    }

    @GetMapping
    public List<Author> getAll() {
        return authorService.getAllAuthors();
    }

    @GetMapping("/{id}")
    public Author getById(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }

    @GetMapping("/{id}/books")
    public List<?> getBooksByAuthor(@PathVariable Long id) {
        return bookService.getBooksByAuthorId(id);
    }

    @GetMapping("/cicd-test")
    public String cicdTest() {
        return "CI/CD test successful";
    }

    @GetMapping("/search")
    public List<Author> searchByName(@RequestParam String q) {
        return authorService.findAuthorsByName(q);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return "Author deleted with id: " + id;
    }
}
