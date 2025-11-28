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
        log.info("═══════════════════════════════════════════");
        log.info("📊 Generando estadísticas del dashboard");
        log.info("═══════════════════════════════════════════");

        // Obtener datos de todos los servicios
        Long totalBooks = getTotalBooks();
        Long totalUsers = getTotalUsers();
        Long totalLoans = getTotalLoans();
        Long activeLoans = getActiveLoans();
        Long overdueLoans = getOverdueLoans();
        Long availableBooks = getAvailableBooks();
        Long loanedBooks = Math.max(0L, totalBooks - availableBooks);
        BigDecimal revenue = getRevenue();

        log.info("📈 Resultados:");
        log.info("   - Total libros: {}", totalBooks);
        log.info("   - Total usuarios: {}", totalUsers);
        log.info("   - Total préstamos: {}", totalLoans);
        log.info("   - Préstamos activos: {}", activeLoans);
        log.info("   - Préstamos vencidos: {}", overdueLoans);
        log.info("   - Libros disponibles: {}", availableBooks);
        log.info("═══════════════════════════════════════════");

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
            String url = microservicesConfig.getBookCatalog().getUrl() + "/api/books/all";
            log.info("📚 Obteniendo libros de: {}", url);
            
            List<?> books = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            
            long count = books != null ? books.size() : 0L;
            log.info("✅ Total libros: {}", count);
            return count;
        } catch (Exception e) {
            log.error("❌ Error obteniendo libros: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getTotalUsers() {
        try {
            String url = microservicesConfig.getUserManagement().getUrl() + "/api/users";
            log.info("👥 Obteniendo usuarios de: {}", url);
            
            List<?> users = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            
            long count = users != null ? users.size() : 0L;
            log.info("✅ Total usuarios: {}", count);
            return count;
        } catch (Exception e) {
            log.error("❌ Error obteniendo usuarios: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getTotalLoans() {
        try {
            // Usar el endpoint GET /api/loans que retorna todos los préstamos
            String url = microservicesConfig.getLoanManagement().getUrl() + "/api/loans";
            log.info("📋 Obteniendo préstamos de: {}", url);
            
            List<?> loans = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            
            long count = loans != null ? loans.size() : 0L;
            log.info("✅ Total préstamos: {}", count);
            return count;
        } catch (Exception e) {
            log.error("❌ Error obteniendo préstamos: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getActiveLoans() {
        try {
            // Obtener todos los préstamos y filtrar los activos (ACTIVE o PENDING)
            String url = microservicesConfig.getLoanManagement().getUrl() + "/api/loans";
            log.info("📋 Obteniendo préstamos activos de: {}", url);
            
            List<Map<String, Object>> loans = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Map.class)
                    .map(map -> (Map<String, Object>) map)
                    .collectList()
                    .block();
            
            if (loans != null) {
                long count = loans.stream()
                        .filter(loan -> {
                            Object status = loan.get("status");
                            return "ACTIVE".equals(status) || "PENDING".equals(status);
                        })
                        .count();
                log.info("✅ Préstamos activos: {}", count);
                return count;
            }
        } catch (Exception e) {
            log.error("❌ Error obteniendo préstamos activos: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getOverdueLoans() {
        try {
            String url = microservicesConfig.getLoanManagement().getUrl() + "/api/loans/overdue";
            log.info("⚠️ Obteniendo préstamos vencidos de: {}", url);
            
            List<?> loans = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            
            long count = loans != null ? loans.size() : 0L;
            log.info("✅ Préstamos vencidos: {}", count);
            return count;
        } catch (Exception e) {
            log.error("❌ Error obteniendo préstamos vencidos: {}", e.getMessage());
        }
        return 0L;
    }

    private Long getAvailableBooks() {
        try {
            String url = microservicesConfig.getBookCatalog().getUrl() + "/api/books/all";
            log.info("📚 Obteniendo libros disponibles de: {}", url);
            
            List<Map<String, Object>> books = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Map.class)
                    .map(map -> (Map<String, Object>) map)
                    .collectList()
                    .block();
            
            if (books != null) {
                long count = books.stream()
                        .filter(book -> {
                            Object availableCopies = book.get("availableCopies");
                            return availableCopies != null && ((Number) availableCopies).intValue() > 0;
                        })
                        .count();
                log.info("✅ Libros disponibles: {}", count);
                return count;
            }
        } catch (Exception e) {
            log.error("❌ Error obteniendo libros disponibles: {}", e.getMessage());
        }
        return 0L;
    }

    private BigDecimal getRevenue() {
        // Implementación simplificada - calcular ingresos por multas
        return BigDecimal.ZERO;
    }
}
