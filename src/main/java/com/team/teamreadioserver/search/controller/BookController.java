package com.team.teamreadioserver.search.controller;

import com.team.teamreadioserver.search.dto.BookRequestDTO;
import com.team.teamreadioserver.search.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
//@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/book")
    public String searchBooks(@RequestParam("query") String query) {
        BookRequestDTO bookRequestDTO = new BookRequestDTO(query);
        return bookService.searchBooks(bookRequestDTO);
    }
}
