package com.example.SpringTest;

import com.example.SpringTest.model.Book;
import com.example.SpringTest.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class BookControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        // Clear repository before each test
        bookRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void testCreateBook_Success() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("978-0-123456-78-9");
        book.setDescription("A test book description");
        book.setGenre("Fiction");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setPublicationYear(2023);
        book.setStockQuantity(10);

        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.isbn").value("978-0-123456-78-9"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser
    void testCreateBook_ValidationError() throws Exception {
        Book book = new Book();
        // Missing required fields

        mockMvc.perform(post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @WithMockUser
    void testGetAllBooks() throws Exception {
        // Create test data
        Book book1 = createTestBook("Book 1", "Author 1");
        Book book2 = createTestBook("Book 2", "Author 2");
        bookRepository.save(book1);
        bookRepository.save(book2);

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[1].title").value("Book 2"));
    }

    @Test
    @WithMockUser
    void testGetBookById_Success() throws Exception {
        Book book = createTestBook("Test Book", "Test Author");
        Book savedBook = bookRepository.save(book);

        mockMvc.perform(get("/api/v1/books/{id}", savedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    @WithMockUser
    void testGetBookById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/books/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with id: 999"));
    }

    @Test
    @WithMockUser
    void testUpdateBook_Success() throws Exception {
        Book book = createTestBook("Original Title", "Original Author");
        Book savedBook = bookRepository.save(book);

        Book updateData = new Book();
        updateData.setTitle("Updated Title");
        updateData.setAuthor("Updated Author");

        mockMvc.perform(put("/api/v1/books/{id}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.author").value("Updated Author"));
    }

    @Test
    @WithMockUser
    void testDeleteBook_Success() throws Exception {
        Book book = createTestBook("Book to Delete", "Author");
        Book savedBook = bookRepository.save(book);

        mockMvc.perform(delete("/api/v1/books/{id}", savedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));

        // Verify book is deleted
        mockMvc.perform(get("/api/v1/books/{id}", savedBook.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetBooksWithPagination() throws Exception {
        // Create test data
        for (int i = 1; i <= 15; i++) {
            Book book = createTestBook("Book " + i, "Author " + i);
            bookRepository.save(book);
        }

        mockMvc.perform(get("/api/v1/books/pageable")
                .param("page", "0")
                .param("size", "5")
                .param("sortBy", "title")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books", hasSize(5)))
                .andExpect(jsonPath("$.totalItems").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    @WithMockUser
    void testSearchBooks() throws Exception {
        Book book1 = createTestBook("Java Programming", "John Doe");
        book1.setGenre("Programming");
        book1.setPrice(BigDecimal.valueOf(29.99));
        
        Book book2 = createTestBook("Python Guide", "Jane Smith");
        book2.setGenre("Programming");
        book2.setPrice(BigDecimal.valueOf(24.99));

        bookRepository.save(book1);
        bookRepository.save(book2);

        mockMvc.perform(get("/api/v1/books/search")
                .param("genre", "Programming")
                .param("minPrice", "25.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books", hasSize(1)))
                .andExpect(jsonPath("$.books[0].title").value("Java Programming"));
    }

    @Test
    @WithMockUser
    void testFullTextSearch() throws Exception {
        Book book1 = createTestBook("Advanced Java", "Expert Author");
        book1.setDescription("Comprehensive guide to Java programming");
        
        Book book2 = createTestBook("Python Basics", "Beginner Author");
        book2.setDescription("Introduction to Python programming");

        bookRepository.save(book1);
        bookRepository.save(book2);

        mockMvc.perform(get("/api/v1/books/search/text")
                .param("q", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books", hasSize(1)))
                .andExpect(jsonPath("$.books[0].title").value("Advanced Java"));
    }

    @Test
    @WithMockUser
    void testGetBookStatistics() throws Exception {
        // Create test books with different genres
        Book fiction = createTestBook("Fiction Book", "Author 1");
        fiction.setGenre("Fiction");
        fiction.setPrice(BigDecimal.valueOf(15.99));
        
        Book sciFi = createTestBook("Sci-Fi Book", "Author 2");
        sciFi.setGenre("Science Fiction");
        sciFi.setPrice(BigDecimal.valueOf(18.99));

        bookRepository.save(fiction);
        bookRepository.save(sciFi);

        mockMvc.perform(get("/api/v1/books/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBooks").value(2))
                .andExpect(jsonPath("$.booksByGenre").exists())
                .andExpect(jsonPath("$.booksByAuthor").exists())
                .andExpect(jsonPath("$.averagePrice").exists());
    }

    @Test
    @WithMockUser
    void testGetAvailableBooks() throws Exception {
        Book availableBook = createTestBook("Available Book", "Author");
        availableBook.setAvailable(true);
        
        Book unavailableBook = createTestBook("Unavailable Book", "Author");
        unavailableBook.setAvailable(false);

        bookRepository.save(availableBook);
        bookRepository.save(unavailableBook);

        mockMvc.perform(get("/api/v1/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books", hasSize(1)))
                .andExpect(jsonPath("$.books[0].title").value("Available Book"));
    }

    private Book createTestBook(String title, String author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setAvailable(true);
        book.setStockQuantity(10);
        return book;
    }
} 