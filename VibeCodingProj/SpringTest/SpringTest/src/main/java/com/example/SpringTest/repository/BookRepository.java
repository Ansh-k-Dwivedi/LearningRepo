package com.example.SpringTest.repository;

import com.example.SpringTest.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Find by author
    List<Book> findByAuthor(String author);
    
    // Find by author with pagination
    Page<Book> findByAuthor(String author, Pageable pageable);

    // Find by title (case insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Find by title with pagination
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Find by author (case insensitive)
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    // Find by genre
    List<Book> findByGenre(String genre);
    
    // Find by genre with pagination
    Page<Book> findByGenre(String genre, Pageable pageable);

    // Find by publication year
    List<Book> findByPublicationYear(Integer year);

    // Find by price range
    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find by price range with pagination
    Page<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Find available books
    List<Book> findByAvailableTrue();
    
    // Find available books with pagination
    Page<Book> findByAvailableTrue(Pageable pageable);

    // Find by ISBN
    Optional<Book> findByIsbn(String isbn);

    // Check if ISBN exists
    boolean existsByIsbn(String isbn);

    // Find books with stock
    List<Book> findByStockQuantityGreaterThan(Integer quantity);

    // Advanced search query
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:genre IS NULL OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) AND " +
           "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR b.price <= :maxPrice) AND " +
           "(:minYear IS NULL OR b.publicationYear >= :minYear) AND " +
           "(:maxYear IS NULL OR b.publicationYear <= :maxYear) AND " +
           "(:available IS NULL OR b.available = :available)")
    Page<Book> findBooksWithFilters(
            @Param("title") String title,
            @Param("author") String author,
            @Param("genre") String genre,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minYear") Integer minYear,
            @Param("maxYear") Integer maxYear,
            @Param("available") Boolean available,
            Pageable pageable
    );

    // Get books count by genre
    @Query("SELECT b.genre, COUNT(b) FROM Book b WHERE b.genre IS NOT NULL GROUP BY b.genre")
    List<Object[]> getBookCountByGenre();

    // Get books count by author
    @Query("SELECT b.author, COUNT(b) FROM Book b GROUP BY b.author")
    List<Object[]> getBookCountByAuthor();

    // Find top expensive books
    List<Book> findTop10ByOrderByPriceDesc();

    // Find recently added books
    List<Book> findTop10ByOrderByCreatedAtDesc();

    // Find books by multiple authors
    @Query("SELECT b FROM Book b WHERE b.author IN :authors")
    List<Book> findByAuthorIn(@Param("authors") List<String> authors);

    // Full text search simulation (for H2)
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Book> searchBooks(@Param("searchTerm") String searchTerm, Pageable pageable);
}