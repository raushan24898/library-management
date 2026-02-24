package com.example.library_management.repository;

import com.example.library_management.model.Author;
import java.util.List;

public interface AuthorRepositoryCustom {
    List<Author> findByNameContaining(String partialName);
}
