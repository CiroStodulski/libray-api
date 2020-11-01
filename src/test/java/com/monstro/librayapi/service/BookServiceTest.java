package com.monstro.librayapi.service;

import com.monstro.librayapi.exception.BusinessException;
import com.monstro.librayapi.model.entity.Book;
import com.monstro.librayapi.model.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    private BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.bookService = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save book with successfully")
    public void saveBook(){
        Book book = createValidBook(Book.builder());
        Mockito.when(repository.existsByIsbn(Mockito.anyString()))
                .thenReturn(false);

        Mockito.when(repository.save(book))
                .thenReturn(createValidBook(Book.builder().id((long) 11)));

        Book savedBook = bookService.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
    }

    private Book createValidBook(Book.BookBuilder builder) {
        return builder.isbn("11234").author("cest").title("test").build();
    }

    @Test
    @DisplayName("Should throw error in add book with isbn already in used")
    public void saveBookAlreadyException(){
        Book book = createValidBook(Book.builder());
        Mockito.when(repository.existsByIsbn(Mockito.anyString()))
                .thenReturn(true);


        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn already register");

        Mockito.verify(repository, Mockito.never()).save(book);
    }
}
