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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Optional;

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


    @Test
    @DisplayName("Should return book by id")
    public void getByIdTest(){
        int id = 11;
        Book book = createValidBook(Book.builder().id((long) id));
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getById((long) id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());

    }

    @Test
    @DisplayName("Should return empty when book by id not exists")
    public void getEmptyByIdTest(){
        int id = 11;

        Optional<Book> foundBook = bookService.getById((long) id);

        assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete book by id")
    public void deleteByIdTest(){
        int id = 11;
        Book book = createValidBook(Book.builder().id((long) id));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(()-> bookService.delete(book));

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Should return error when book by id not exists")
    public void deleteByIdErrorTest(){
        Book book = new Book();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> bookService.delete(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Should update book by id")
    public void updateByIdTest(){
        int id = 11;
        Book updatingBook = createValidBook(Book.builder().id((long) id));

        Book updateBook  = createValidBook(Book.builder().id((long) id));
        updateBook.setTitle("test");
        Mockito.when(repository.save(updatingBook)).thenReturn(updateBook);

        Book book = bookService.update(updateBook);

        assertThat(book.getId()).isEqualTo(updateBook.getId());
        assertThat(book.getTitle()).isEqualTo(updateBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updateBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updateBook.getAuthor());
    }

    @Test
    @DisplayName("Should return error when book by id not exists")
    public void updateByIdErrorTest(){
        int id = 11;
        Book book = createValidBook(Book.builder().id((long) id));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(()-> bookService.update(book));

        Mockito.verify(repository, Mockito.times(1)).save(book);
    }


    @Test
    @DisplayName("Should return return with property  ")
    public void findBookTest(){

        Book book = createValidBook(Book.builder());

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Book> page = new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 10), 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> books = bookService.find(book, pageRequest);

        assertThat(books.getTotalElements()).isEqualTo(1);
        assertThat(books.getContent()).isEqualTo(books);
        assertThat(books.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(books.getPageable().getPageSize()).isEqualTo(10);
    }

}
