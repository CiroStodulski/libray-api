package com.monstro.librayapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monstro.librayapi.api.dto.BookDTO;
import com.monstro.librayapi.exception.BusinessException;
import com.monstro.librayapi.model.entity.Book;
import com.monstro.librayapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static  String BOOK_API = "/api/book";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Should create book with successfully.")
    public void createBookTest() throws Exception {

        BookDTO dto = createNewBookDTO();
        Book savedBook = Book.builder().id((long) 10).author("artur").title("test").isbn("001").build();

        BDDMockito
                .given(bookService.save(Mockito.any(Book.class)))
                .willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    private BookDTO createNewBookDTO() {
        return BookDTO.builder().author("artur").title("test").isbn("001").build();
    }

    @Test
    @DisplayName("Should create book with error while book not provider props .")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }


    @Test
    @DisplayName("Should throw error in add book with isbn already in used")
    public void createInvalidBookWithDuplicationIsbnTest() throws Exception{

        BookDTO dto = createNewBookDTO();

        String json = new ObjectMapper().writeValueAsString(dto);

        String messageError = "Isbn already in used";
        BDDMockito
                .given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(messageError));


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(messageError));

    }

    @Test
    @DisplayName("Should return info by a book")
    public void getBookTest() throws Exception{

        Long id = Long.valueOf(12);


        Book book =  Book.builder().id((long) 10).author("artur").title("test").isbn("001").build();

        BDDMockito
                .given(bookService.getById(id))
                .willReturn(Optional.of(book));


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));

    }

    @Test
    @DisplayName("Should return error not found a book")
    public void getBookNotFoundTest() throws Exception{
        BDDMockito
                .given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+1));

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete a book")
    public void deleteBookTest() throws Exception{
        BDDMockito
                .given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(Book.builder().id((long) 11).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+1));

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should not found a book in delete")
    public void deleteNotFoundBookTest() throws Exception{
        BDDMockito
                .given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+1));

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update a book in delete")
    public void updateBookTest() throws Exception{
        Long id = Long.valueOf(11);
        BookDTO dto = createNewBookDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder()
                .id((long) 11)
                .title("11")
                .author("11")
                .isbn("11")
                .build();

        Book updateBook = Book.builder()
                .id((long) 11)
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .build();

        BDDMockito
                .given(bookService.getById(id))
                .willReturn(Optional.of(book));

        BDDMockito
                .given(bookService.update(updateBook))
                .willReturn(updateBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+id))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(updateBook.getTitle()))
                .andExpect(jsonPath("author").value(updateBook.getAuthor()))
                .andExpect(jsonPath("isbn").value(updateBook.getIsbn()));
    }

    @Test
    @DisplayName("Should return 404 update a book in delete")
    public void updateBookNotFoundTest() throws Exception{
        String json = new ObjectMapper().writeValueAsString(createNewBookDTO());

        BDDMockito
                .given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter books")
    public void findBooksTest() throws Exception{
        Long id = Long.valueOf(11);

        Book book = Book.builder()
                .id((long) 11)
                .title("11")
                .author("11")
                .isbn("11")
                .build();


        BDDMockito
                .given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,100),1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(queryString)).accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
}
