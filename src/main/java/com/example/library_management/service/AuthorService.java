package com.example.library_management.service;

import com.example.library_management.model.Author;
import java.util.List;

public interface AuthorService {
    Author createAuthor(Author author);
    Author getAuthorById(Long id);
    List<Author> getAllAuthors();
    List<Author> findAuthorsByName(String partialName);
    void deleteAuthor(Long id);
}
