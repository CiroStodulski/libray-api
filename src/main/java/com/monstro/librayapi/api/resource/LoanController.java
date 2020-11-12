package com.monstro.librayapi.api.resource;

import com.monstro.librayapi.api.dto.LoanDTO;
import com.monstro.librayapi.model.entity.Book;
import com.monstro.librayapi.model.entity.Loan;
import com.monstro.librayapi.service.BookService;
import com.monstro.librayapi.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loan")
public class LoanController {

    private BookService bookService;
    private LoanService loanService;
    private ModelMapper modelMapper;

    public LoanController(BookService bookService,LoanService loanService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto){

        Book book = bookService.getByIsbn(dto.getIsbn()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"book not found for passed isbn"));

        Loan loan = Loan.builder().book(book).customer(dto.getCustomer()).loanData(LocalDate.now()).build();

        loan = loanService.save(loan);
        return  loan.getId();
    }

}
