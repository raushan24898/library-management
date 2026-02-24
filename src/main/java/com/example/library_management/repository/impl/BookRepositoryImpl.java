package com.example.library_management.repository.impl;

import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRepositoryImpl implements BookRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

//    @Override
//    public List<Book> findBooksByGenre(String genre) {
//        return em.createQuery("SELECT b FROM Book b WHERE b.genre = :genre", Book.class)
//                .setParameter("genre", genre)
//                .getResultList();
//    }

    @Override
    public List<Book> findBooksByAuthorId(Long authorId) {
        return em.createQuery("SELECT b FROM Book b WHERE b.author.id = :authorId", Book.class)
                .setParameter("authorId", authorId)
                .getResultList();
    }
}

