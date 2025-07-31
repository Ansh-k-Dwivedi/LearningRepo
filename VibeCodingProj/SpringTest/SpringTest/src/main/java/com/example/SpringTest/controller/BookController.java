package com.example.SpringTest.controller;

import com.example.SpringTest.model.Book;
import com.example.SpringTest.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Book Management", description = "APIs for managing books in the library system")
@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin(origins = "*")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @Operation(summary = "Create a new book", description = "Add a new book to the library system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Book already exists")
    })
    @PostMapping
    public ResponseEntity<Book> createBook(
            @Parameter(description = "Book details to be created", required = true)
            @Valid @RequestBody Book book) {
        logger.info("Creating book: {}", book.getTitle());
        Book createdBook = bookService.createBook(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all books", description = "Retrieve all books from the library")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        logger.info("Fetching all books");
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Get books with pagination", description = "Retrieve books with pagination, sorting, and filtering")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully with pagination")
    @GetMapping("/pageable")
    public ResponseEntity<Map<String, Object>> getAllBooksPageable(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.info("Fetching books with pagination - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                    page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> booksPage = bookService.getAllBooksPageable(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("books", booksPage.getContent());
        response.put("currentPage", booksPage.getNumber());
        response.put("totalItems", booksPage.getTotalElements());
        response.put("totalPages", booksPage.getTotalPages());
        response.put("pageSize", booksPage.getSize());
        response.put("hasNext", booksPage.hasNext());
        response.put("hasPrevious", booksPage.hasPrevious());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Advanced search", description = "Search books with multiple filters")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(
            @Parameter(description = "Title filter") @RequestParam(required = false) String title,
            @Parameter(description = "Author filter") @RequestParam(required = false) String author,
            @Parameter(description = "Genre filter") @RequestParam(required = false) String genre,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Minimum publication year") @RequestParam(required = false) Integer minYear,
            @Parameter(description = "Maximum publication year") @RequestParam(required = false) Integer maxYear,
            @Parameter(description = "Availability filter") @RequestParam(required = false) Boolean available,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {

        logger.info("Advanced search with filters");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> booksPage = bookService.searchBooksWithFilters(
            title, author, genre, minPrice, maxPrice, minYear, maxYear, available, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("books", booksPage.getContent());
        response.put("currentPage", booksPage.getNumber());
        response.put("totalItems", booksPage.getTotalElements());
        response.put("totalPages", booksPage.getTotalPages());
        response.put("filters", Map.of(
            "title", title != null ? title : "",
            "author", author != null ? author : "",
            "genre", genre != null ? genre : "",
            "minPrice", minPrice != null ? minPrice : "",
            "maxPrice", maxPrice != null ? maxPrice : "",
            "available", available != null ? available : ""
        ));
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Full-text search", description = "Search books by keyword across multiple fields")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    @GetMapping("/search/text")
    public ResponseEntity<Map<String, Object>> fullTextSearch(
            @Parameter(description = "Search keyword", required = true) @RequestParam String q,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        logger.info("Full-text search for: {}", q);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> booksPage = bookService.fullTextSearch(q, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("books", booksPage.getContent());
        response.put("currentPage", booksPage.getNumber());
        response.put("totalItems", booksPage.getTotalElements());
        response.put("totalPages", booksPage.getTotalPages());
        response.put("searchTerm", q);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book found"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(
            @Parameter(description = "Book ID", required = true) @PathVariable Long id) {
        logger.info("Fetching book with id: {}", id);
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Get books by author", description = "Retrieve all books by a specific author")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(
            @Parameter(description = "Author name", required = true) @PathVariable String author) {
        logger.info("Fetching books by author: {}", author);
        List<Book> books = bookService.getBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Update book", description = "Update an existing book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @Parameter(description = "Book ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated book details", required = true) @Valid @RequestBody Book bookDetails) {
        logger.info("Updating book with id: {}", id);
        Book updatedBook = bookService.updateBook(id, bookDetails);
        return ResponseEntity.ok(updatedBook);
    }

    @Operation(summary = "Delete book", description = "Delete a book from the library")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(
            @Parameter(description = "Book ID", required = true) @PathVariable Long id) {
        logger.info("Deleting book with id: {}", id);
        bookService.deleteBook(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book deleted successfully");
        response.put("id", id.toString());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get book statistics", description = "Retrieve statistics about the book collection")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBookStats() {
        logger.info("Fetching book statistics");
        
        Map<String, Object> stats = bookService.getBookStatistics();
        
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Check if book exists", description = "Check if a book exists by ID")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkBookExists(
            @Parameter(description = "Book ID", required = true) @PathVariable Long id) {
        logger.info("Checking if book exists with id: {}", id);
        
        boolean exists = bookService.existsById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get available books", description = "Retrieve all available books")
    @ApiResponse(responseCode = "200", description = "Available books retrieved successfully")
    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableBooks(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Fetching available books");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> availableBooks = bookService.getAvailableBooks(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("books", availableBooks.getContent());
        response.put("currentPage", availableBooks.getNumber());
        response.put("totalItems", availableBooks.getTotalElements());
        response.put("totalPages", availableBooks.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
}