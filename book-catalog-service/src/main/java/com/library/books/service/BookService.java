package com.library.books.service;

import com.library.books.dto.*;
import com.library.books.model.Book;
import com.library.books.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponseDTO createBook(BookCreateDTO createDTO) {
        log.info("Creando nuevo libro: {}", createDTO.getTitle());

        if (createDTO.getIsbn() != null && !createDTO.getIsbn().isEmpty()) {
            bookRepository.findByIsbn(createDTO.getIsbn())
                    .ifPresent(book -> {
                        throw new RuntimeException("Ya existe un libro con el ISBN: " + createDTO.getIsbn());
                    });
        }

        Book book = Book.builder()
                .title(createDTO.getTitle())
                .author(createDTO.getAuthor())
                .isbn(createDTO.getIsbn())
                .category(createDTO.getCategory())
                .publisher(createDTO.getPublisher())
                .year(createDTO.getYear())
                .description(createDTO.getDescription())
                .coverUrl(createDTO.getCoverUrl())
                .totalCopies(createDTO.getTotalCopies())
                .availableCopies(createDTO.getTotalCopies())
                .price(createDTO.getPrice())
                .featured(createDTO.getFeatured() != null ? createDTO.getFeatured() : false)
                .build();

        book = bookRepository.save(book);
        log.info("Libro creado exitosamente con ID: {}", book.getId());

        return BookResponseDTO.fromEntity(book);
    }

    public BookResponseDTO getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        return BookResponseDTO.fromEntity(book);
    }

    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookResponseDTO::fromEntity);
    }

    public Page<BookResponseDTO> searchBooks(String query, Pageable pageable) {
        return bookRepository.searchBooks(query, pageable)
                .map(BookResponseDTO::fromEntity);
    }

    public Page<BookResponseDTO> getBooksByCategory(String category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable)
                .map(BookResponseDTO::fromEntity);
    }

    public Page<BookResponseDTO> getFeaturedBooks(Pageable pageable) {
        return bookRepository.findByFeaturedTrue(pageable)
                .map(BookResponseDTO::fromEntity);
    }

    @Transactional
    public BookResponseDTO updateBook(Long bookId, BookUpdateDTO updateDTO) {
        log.info("Actualizando libro: {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        if (updateDTO.getTitle() != null) {
            book.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getAuthor() != null) {
            book.setAuthor(updateDTO.getAuthor());
        }
        if (updateDTO.getIsbn() != null) {
            if (!updateDTO.getIsbn().equals(book.getIsbn())) {
                bookRepository.findByIsbn(updateDTO.getIsbn())
                        .ifPresent(b -> {
                            throw new RuntimeException("Ya existe un libro con el ISBN: " + updateDTO.getIsbn());
                        });
            }
            book.setIsbn(updateDTO.getIsbn());
        }
        if (updateDTO.getCategory() != null) {
            book.setCategory(updateDTO.getCategory());
        }
        if (updateDTO.getPublisher() != null) {
            book.setPublisher(updateDTO.getPublisher());
        }
        if (updateDTO.getYear() != null) {
            book.setYear(updateDTO.getYear());
        }
        if (updateDTO.getDescription() != null) {
            book.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getCoverUrl() != null) {
            book.setCoverUrl(updateDTO.getCoverUrl());
        }
        if (updateDTO.getTotalCopies() != null) {
            int difference = updateDTO.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(updateDTO.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + difference);
        }
        if (updateDTO.getPrice() != null) {
            book.setPrice(updateDTO.getPrice());
        }
        if (updateDTO.getFeatured() != null) {
            book.setFeatured(updateDTO.getFeatured());
        }

        book = bookRepository.save(book);
        return BookResponseDTO.fromEntity(book);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        log.info("Eliminando libro: {}", bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        bookRepository.delete(book);
    }

    public BookAvailabilityDTO checkAvailability(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        boolean available = book.getAvailableCopies() > 0;

        return BookAvailabilityDTO.builder()
                .bookId(bookId)
                .available(available)
                .availableCopies(book.getAvailableCopies())
                .totalCopies(book.getTotalCopies())
                .message(available
                        ? "El libro está disponible"
                        : "El libro no está disponible")
                .build();
    }

    @Transactional
    public BookResponseDTO updateCopies(Long bookId, Integer copiesChange) {
        log.info("Actualizando copias del libro {}: {}", bookId, copiesChange);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        book.updateAvailableCopies(copiesChange);
        book = bookRepository.save(book);

        return BookResponseDTO.fromEntity(book);
    }
}


