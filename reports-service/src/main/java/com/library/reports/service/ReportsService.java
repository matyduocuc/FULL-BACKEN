package com.library.reports.service;

import com.library.reports.config.MicroservicesConfig;
import com.library.reports.dto.DashboardStatisticsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsService {

    private final WebClient.Builder webClientBuilder;
    private final MicroservicesConfig microservicesConfig;

    public DashboardStatisticsDTO getDashboardStatistics() {
        log.info("Generando estadísticas del dashboard");

        // Obtener datos de todos los servicios
        Long totalBooks = getTotalBooks();
        Long totalUsers = getTotalUsers();
        Long totalLoans = getTotalLoans();
        Long activeLoans = getActiveLoans();
        Long overdueLoans = getOverdueLoans();
        Long availableBooks = getAvailableBooks();
        Long loanedBooks = totalBooks - availableBooks;
        BigDecimal revenue = getRevenue();

        return DashboardStatisticsDTO.builder()
                .totalBooks(totalBooks)
                .totalUsers(totalUsers)
                .totalLoans(totalLoans)
                .activeLoans(activeLoans)
                .overdueLoans(overdueLoans)
                .availableBooks(availableBooks)
                .loanedBooks(loanedBooks)
                .revenue(revenue)
                .dateRange("Últimos 30 días")
                .build();
    }

    private Long getTotalBooks() {
        try {
            String url = microservicesConfig.getBookCatalog().getUrl() + "/api/books?size=1";
            Map<String, Object> response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("totalElements")) {
                return ((Number) response.get("totalElements")).longValue();
            }
        } catch (Exception e) {
            log.error("Error obteniendo total de libros: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getTotalUsers() {
        try {
            String url = microservicesConfig.getUserManagement().getUrl() + "/api/users";
            List<?> users = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            return users != null ? (long) users.size() : 0L;
        } catch (Exception e) {
            log.error("Error obteniendo total de usuarios: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getTotalLoans() {
        try {
            String url = microservicesConfig.getLoanManagement().getUrl() + "/api/loans/user/1";
            List<?> loans = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            return loans != null ? (long) loans.size() : 0L;
        } catch (Exception e) {
            log.error("Error obteniendo total de préstamos: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getActiveLoans() {
        try {
            String url = microservicesConfig.getLoanManagement().getUrl() + "/api/loans/overdue";
            List<?> loans = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            return loans != null ? (long) loans.size() : 0L;
        } catch (Exception e) {
            log.error("Error obteniendo préstamos activos: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getOverdueLoans() {
        try {
            String url = microservicesConfig.getLoanManagement().getUrl() + "/api/loans/overdue";
            List<?> loans = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            return loans != null ? (long) loans.size() : 0L;
        } catch (Exception e) {
            log.error("Error obteniendo préstamos vencidos: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getAvailableBooks() {
        try {
            String url = microservicesConfig.getBookCatalog().getUrl() + "/api/books?size=1000";
            Map<String, Object> response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("content")) {
                List<Map<String, Object>> books = (List<Map<String, Object>>) response.get("content");
                return books.stream()
                        .filter(book -> {
                            Object availableCopies = book.get("availableCopies");
                            return availableCopies != null && ((Number) availableCopies).intValue() > 0;
                        })
                        .count();
            }
        } catch (Exception e) {
            log.error("Error obteniendo libros disponibles: {}", e.getMessage());
        }
        return 0L;
    }

    private BigDecimal getRevenue() {
        // Implementación simplificada
        return BigDecimal.ZERO;
    }
}


