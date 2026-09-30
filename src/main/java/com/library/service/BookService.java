package com.library.service;

import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Add a new book
    public Book addBook(Book book) {

        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new RuntimeException("A book with this ISBN already exists");
        }

        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new RuntimeException(
                    "Available copies cannot exceed total copies");
        }

        return bookRepository.save(book);
    }

    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Get book by ID
    public Book getBookById(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    // Update an existing book
    public Book updateBook(Long id, Book updatedBook) {

        Book existingBook = getBookById(id);

        if (!existingBook.getIsbn().equals(updatedBook.getIsbn())
                && bookRepository.findByIsbn(updatedBook.getIsbn()).isPresent()) {

            throw new RuntimeException(
                    "A book with this ISBN already exists");
        }

        if (updatedBook.getAvailableCopies() > updatedBook.getTotalCopies()) {

            throw new RuntimeException(
                    "Available copies cannot exceed total copies");
        }

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setTotalCopies(updatedBook.getTotalCopies());
        existingBook.setAvailableCopies(updatedBook.getAvailableCopies());

        return bookRepository.save(existingBook);
    }

    // Deactivate a book
    public void deactivateBook(Long id) {

        Book book = getBookById(id);

        book.setActive(false);

        bookRepository.save(book);
    }

    // Search books
    public List<Book> searchBooks(String keyword) {

        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword,
                        keyword);
    }
}