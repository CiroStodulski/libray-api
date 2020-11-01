package com.monstro.librayapi.model.respository;

import com.monstro.librayapi.model.entity.Book;
import com.monstro.librayapi.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

        Book book = Book.builder().isbn(isbn).author("cest").title("test").build();
        entityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false with not exists a book in data base with isbn")
    public void getBookByIsbnNotExisttest(){
        String isbn = "123";

        Book book = Book.builder().isbn(isbn).author("cest").title("test").build();
        entityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn("323");


        assertThat(exists).isFalse();
    }

}
