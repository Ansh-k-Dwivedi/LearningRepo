package com.example.SpringTest.service;

import com.example.SpringTest.exception.BookNotFoundException;
import com.example.SpringTest.model.Book;
import com.example.SpringTest.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "'all_books'")
    public List<Book> getAllBooks() {
        logger.debug("Fetching all books");
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Book> getAllBooksPageable(Pageable pageable) {
        logger.debug("Fetching books with pagination: {}", pageable);
        return bookRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "#id")
    public Book getBookById(Long id) {
        logger.debug("Fetching book with id: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Book not found with id: {}", id);
                    return new BookNotFoundException("Book not found with id: " + id);
                });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "'author:' + #author")
    public List<Book> getBooksByAuthor(String author) {
        logger.debug("Fetching books by author: {}", author);
        return bookRepository.findByAuthor(author);
    }

    @CacheEvict(value = "books", allEntries = true)
    public Book createBook(Book book) {
        logger.debug("Creating new book: {}", book.getTitle());
        
        // Business logic: Check for duplicates
        if (isDuplicateBook(book)) {
            logger.warn("Duplicate book detected: {} by {}", book.getTitle(), book.getAuthor());
            throw new IllegalArgumentException("Book with same title and author already exists");
        }

        // Check ISBN uniqueness if provided
        if (book.getIsbn() != null && bookRepository.existsByIsbn(book.getIsbn())) {
            logger.warn("Book with ISBN {} already exists", book.getIsbn());
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        
        Book savedBook = bookRepository.save(book);
        logger.info("Book created successfully with id: {}", savedBook.getId());
        return savedBook;
    }

    @CacheEvict(value = "books", key = "#id")
    public Book updateBook(Long id, Book bookDetails) {
        logger.debug("Updating book with id: {}", id);
        
        Book existingBook = getBookById(id); // This will throw exception if not found
        
        // Update fields
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        if (bookDetails.getIsbn() != null) {
            existingBook.setIsbn(bookDetails.getIsbn());
        }
        if (bookDetails.getDescription() != null) {
            existingBook.setDescription(bookDetails.getDescription());
        }
        if (bookDetails.getPublicationYear() != null) {
            existingBook.setPublicationYear(bookDetails.getPublicationYear());
        }
        if (bookDetails.getGenre() != null) {
            existingBook.setGenre(bookDetails.getGenre());
        }
        if (bookDetails.getPrice() != null) {
            existingBook.setPrice(bookDetails.getPrice());
        }
        if (bookDetails.getStockQuantity() != null) {
            existingBook.setStockQuantity(bookDetails.getStockQuantity());
        }
        if (bookDetails.getAvailable() != null) {
            existingBook.setAvailable(bookDetails.getAvailable());
        }
        
        Book updatedBook = bookRepository.save(existingBook);
        logger.info("Book updated successfully with id: {}", updatedBook.getId());
        return updatedBook;
    }

    @CacheEvict(value = "books", allEntries = true)
    public void deleteBook(Long id) {
        logger.debug("Deleting book with id: {}", id);
        
        Book book = getBookById(id); // This will throw exception if not found
        bookRepository.delete(book);
        logger.info("Book deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "book_stats", key = "'total_count'")
    public long getTotalCount() {
        return bookRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<Book> searchBooksWithFilters(String title, String author, String genre, 
                                           BigDecimal minPrice, BigDecimal maxPrice, 
                                           Integer minYear, Integer maxYear, 
                                           Boolean available, Pageable pageable) {
        logger.debug("Searching books with filters");
        return bookRepository.findBooksWithFilters(title, author, genre, minPrice, maxPrice, 
                                                 minYear, maxYear, available, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Book> fullTextSearch(String searchTerm, Pageable pageable) {
        logger.debug("Performing full-text search for: {}", searchTerm);
        return bookRepository.searchBooks(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Book> getAvailableBooks(Pageable pageable) {
        logger.debug("Fetching available books");
        return bookRepository.findByAvailableTrue(pageable);
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByGenre(String genre) {
        logger.debug("Fetching books by genre: {}", genre);
        return bookRepository.findByGenre(genre);
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.debug("Fetching books by price range: {} - {}", minPrice, maxPrice);
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByPublicationYear(Integer year) {
        logger.debug("Fetching books by publication year: {}", year);
        return bookRepository.findByPublicationYear(year);
    }

    @Transactional(readOnly = true)
    public List<Book> getTopExpensiveBooks() {
        logger.debug("Fetching top expensive books");
        return bookRepository.findTop10ByOrderByPriceDesc();
    }

    @Transactional(readOnly = true)
    public List<Book> getRecentlyAddedBooks() {
        logger.debug("Fetching recently added books");
        return bookRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "book_stats", key = "'statistics'")
    public Map<String, Object> getBookStatistics() {
        logger.debug("Generating book statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        stats.put("totalBooks", bookRepository.count());
        stats.put("availableBooks", bookRepository.findByAvailableTrue().size());
        stats.put("unavailableBooks", bookRepository.count() - bookRepository.findByAvailableTrue().size());
        
        // Genre statistics
        List<Object[]> genreStats = bookRepository.getBookCountByGenre();
        Map<String, Long> genreMap = genreStats.stream()
                .collect(Collectors.toMap(
                    arr -> (String) arr[0],
                    arr -> (Long) arr[1]
                ));
        stats.put("booksByGenre", genreMap);
        
        // Author statistics
        List<Object[]> authorStats = bookRepository.getBookCountByAuthor();
        Map<String, Long> authorMap = authorStats.stream()
                .collect(Collectors.toMap(
                    arr -> (String) arr[0],
                    arr -> (Long) arr[1]
                ));
        stats.put("booksByAuthor", authorMap);
        
        // Price statistics
        List<Book> allBooks = bookRepository.findAll();
        if (!allBooks.isEmpty()) {
            double avgPrice = allBooks.stream()
                    .filter(book -> book.getPrice() != null)
                    .mapToDouble(book -> book.getPrice().doubleValue())
                    .average()
                    .orElse(0.0);
            stats.put("averagePrice", BigDecimal.valueOf(avgPrice));
            
            BigDecimal maxPrice = allBooks.stream()
                    .filter(book -> book.getPrice() != null)
                    .map(Book::getPrice)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            stats.put("maxPrice", maxPrice);
            
            BigDecimal minPrice = allBooks.stream()
                    .filter(book -> book.getPrice() != null)
                    .map(Book::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            stats.put("minPrice", minPrice);
        }
        
        // Stock statistics
        long booksInStock = bookRepository.findByStockQuantityGreaterThan(0).size();
        stats.put("booksInStock", booksInStock);
        stats.put("booksOutOfStock", bookRepository.count() - booksInStock);
        
        stats.put("timestamp", System.currentTimeMillis());
        stats.put("generatedAt", java.time.LocalDateTime.now());
        
        return stats;
    }

    private boolean isDuplicateBook(Book book) {
        return bookRepository.findByAuthor(book.getAuthor())
                .stream()
                .anyMatch(existingBook -> 
                    existingBook.getTitle().equalsIgnoreCase(book.getTitle()));
    }
}