package com.monstro.librayapi.service;

import com.monstro.librayapi.exception.BusinessException;
import com.monstro.librayapi.model.entity.Book;
import com.monstro.librayapi.model.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn already register");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Book book) {

    }

    @Override
    public Book update(Book book) {
        return book;
    }
}
