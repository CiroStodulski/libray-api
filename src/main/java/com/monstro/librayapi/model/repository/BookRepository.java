package com.monstro.librayapi.model.repository;

import com.monstro.librayapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository <Book, Integer> {
    boolean existsByIsbn(String isbn);
}
