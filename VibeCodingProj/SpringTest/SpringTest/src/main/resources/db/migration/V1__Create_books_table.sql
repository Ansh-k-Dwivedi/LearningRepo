-- Create books table with all necessary fields
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    description VARCHAR(1000),
    publication_year INTEGER,
    genre VARCHAR(100),
    price DECIMAL(10,2),
    stock_quantity INTEGER DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_book_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_book_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_book_genre ON books(genre);
CREATE INDEX IF NOT EXISTS idx_book_publication_year ON books(publication_year);
CREATE INDEX IF NOT EXISTS idx_book_price ON books(price);
CREATE INDEX IF NOT EXISTS idx_book_available ON books(is_available);
CREATE INDEX IF NOT EXISTS idx_book_created_at ON books(created_at);

-- Insert some sample data
INSERT INTO books (title, author, isbn, description, publication_year, genre, price, stock_quantity, is_available) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 'A classic American novel set in the Jazz Age', 1925, 'Fiction', 12.99, 50, TRUE),
('To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 'A gripping tale of racial injustice and childhood innocence', 1960, 'Fiction', 14.99, 30, TRUE),
('1984', 'George Orwell', '978-0-452-28423-4', 'A dystopian social science fiction novel', 1949, 'Science Fiction', 13.99, 25, TRUE),
('Pride and Prejudice', 'Jane Austen', '978-0-14-143951-8', 'A romantic novel of manners', 1813, 'Romance', 11.99, 40, TRUE),
('The Catcher in the Rye', 'J.D. Salinger', '978-0-316-76948-0', 'A controversial novel about teenage rebellion', 1951, 'Fiction', 13.50, 20, TRUE),
('Lord of the Rings', 'J.R.R. Tolkien', '978-0-544-00341-5', 'An epic high fantasy novel', 1954, 'Fantasy', 25.99, 15, TRUE),
('Harry Potter and the Philosopher''s Stone', 'J.K. Rowling', '978-0-7475-3269-9', 'The first book in the Harry Potter series', 1997, 'Fantasy', 15.99, 100, TRUE),
('The Hobbit', 'J.R.R. Tolkien', '978-0-547-92822-7', 'A children''s fantasy novel', 1937, 'Fantasy', 12.99, 35, TRUE),
('Brave New World', 'Aldous Huxley', '978-0-06-085052-4', 'A dystopian novel about a futuristic society', 1932, 'Science Fiction', 14.50, 28, TRUE),
('The Chronicles of Narnia', 'C.S. Lewis', '978-0-06-440492-1', 'A series of seven fantasy novels', 1950, 'Fantasy', 19.99, 22, TRUE); 