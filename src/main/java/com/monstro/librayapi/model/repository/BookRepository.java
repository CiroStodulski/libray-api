package com.monstro.librayapi.model.repository;

import com.monstro.librayapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository <Book, Integer> {
    boolean existsByIsbn(String isbn);

    Optional<Book> findById(Long id);
}
