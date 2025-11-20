package com.library.books.controller;

import com.library.books.dto.*;
import com.library.books.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Catalog", description = "API para gestión del catálogo de libros")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Crear libro", description = "Crea un nuevo libro en el catálogo")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookCreateDTO createDTO) {
        BookResponseDTO book = bookService.createBook(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "Obtener libro por ID", description = "Obtiene la información de un libro específico")
    public ResponseEntity<BookResponseDTO> getBook(
            @Parameter(description = "ID del libro") @PathVariable Long bookId) {
        BookResponseDTO book = bookService.getBookById(bookId);
        return ResponseEntity.ok(book);
    }

    @GetMapping
    @Operation(summary = "Listar libros", description = "Obtiene una lista paginada de libros con filtros opcionales")
    public ResponseEntity<Page<BookResponseDTO>> getAllBooks(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "ASC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BookResponseDTO> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar libros", description = "Busca libros por título, autor o ISBN")
    public ResponseEntity<Page<BookResponseDTO>> searchBooks(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponseDTO> books = bookService.searchBooks(q, pageable);
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{bookId}")
    @Operation(summary = "Actualizar libro", description = "Actualiza la información de un libro")
    public ResponseEntity<BookResponseDTO> updateBook(
            @Parameter(description = "ID del libro") @PathVariable Long bookId,
            @Valid @RequestBody BookUpdateDTO updateDTO) {
        BookResponseDTO book = bookService.updateBook(bookId, updateDTO);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{bookId}")
    @Operation(summary = "Eliminar libro", description = "Elimina un libro del catálogo")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID del libro") @PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookId}/availability")
    @Operation(summary = "Verificar disponibilidad", description = "Verifica la disponibilidad de un libro")
    public ResponseEntity<BookAvailabilityDTO> checkAvailability(
            @Parameter(description = "ID del libro") @PathVariable Long bookId) {
        BookAvailabilityDTO availability = bookService.checkAvailability(bookId);
        return ResponseEntity.ok(availability);
    }

    @PatchMapping("/{bookId}/copies")
    @Operation(summary = "Actualizar copias", description = "Actualiza el número de copias disponibles de un libro")
    public ResponseEntity<BookResponseDTO> updateCopies(
            @Parameter(description = "ID del libro") @PathVariable Long bookId,
            @RequestParam Integer change) {
        BookResponseDTO book = bookService.updateCopies(bookId, change);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Libros por categoría", description = "Obtiene libros filtrados por categoría")
    public ResponseEntity<Page<BookResponseDTO>> getBooksByCategory(
            @Parameter(description = "Categoría") @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponseDTO> books = bookService.getBooksByCategory(category, pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/featured")
    @Operation(summary = "Libros destacados", description = "Obtiene la lista de libros destacados")
    public ResponseEntity<Page<BookResponseDTO>> getFeaturedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponseDTO> books = bookService.getFeaturedBooks(pageable);
        return ResponseEntity.ok(books);
    }
}


