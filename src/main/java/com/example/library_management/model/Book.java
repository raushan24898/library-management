package com.example.library_management.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;

    // Many books -> One author
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    // prevent serializing Author.books when serializing Author inside Book
    @JsonIgnoreProperties({"books"})
    private Author author;
}