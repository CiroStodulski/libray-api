package com.monstro.librayapi.service;

import com.monstro.librayapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
     Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);
}
