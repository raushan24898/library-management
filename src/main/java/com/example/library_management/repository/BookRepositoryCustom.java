package com.example.library_management.repository;

import com.example.library_management.model.Book;
import java.util.List;

public interface BookRepositoryCustom {
//    List<Book> findBooksByGenre(String genre);
    List<Book> findBooksByAuthorId(Long authorId);
}
