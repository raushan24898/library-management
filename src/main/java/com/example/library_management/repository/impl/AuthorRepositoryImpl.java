package com.example.library_management.repository.impl;

import com.example.library_management.model.Author;
import com.example.library_management.repository.AuthorRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorRepositoryImpl implements AuthorRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Author> findByNameContaining(String partialName) {
        return em.createQuery("SELECT a FROM Author a WHERE LOWER(a.name) LIKE :p", Author.class)
                .setParameter("p", "%" + partialName.toLowerCase() + "%")
                .getResultList();
    }
}
