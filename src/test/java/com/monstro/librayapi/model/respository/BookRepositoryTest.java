package com.monstro.librayapi.model.respository;

import com.monstro.librayapi.model.entity.Book;
import com.monstro.librayapi.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;


    @Test
    @DisplayName("Should return true with exists a book in data base with isbn")
    public void getBookByIsbntest(){
        String isbn = "123";

        Book book = CreateNewBook(isbn);
        entityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    private Book CreateNewBook(String isbn) {
        return Book.builder().isbn(isbn).author("cest").title("test").build();
    }

    @Test
    @DisplayName("Should return false with not exists a book in data base with isbn")
    public void getBookByIsbnNotExistTest(){
        String isbn = "123";

        Book book = CreateNewBook(isbn);
        entityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn("323");


        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return book by id")
    public void getBookByIBookTest(){
        String isbn = "123";

        Book book = CreateNewBook(isbn);
        entityManager.persist(book);

        Optional<Book> foudBook = bookRepository.findById(book.getId());


        assertThat(foudBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should return false with not exists a book in data base with isbn")
    public void deleteBookByIsbnNotExistTest(){
        String isbn = "123";

        Book book = CreateNewBook(isbn);
        entityManager.persist(book);

        Optional<Book> foudBook = bookRepository.findById(book.getId());


        assertThat(foudBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should return book by id")
    public void deleteBookByIBookTest(){
        String isbn = "123";

        Book book = CreateNewBook(isbn);
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());

        bookRepository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();
    }

}
