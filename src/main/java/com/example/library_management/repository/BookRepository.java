package com.example.library_management.repository;

import com.example.library_management.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
    Book findByTitle(String title);
    // Using Native SQL
    @Query(value = "SELECT * FROM books WHERE genre = :genre", nativeQuery = true)
    List<Book> findBooksByGenre(@Param("genre") String genre);
}