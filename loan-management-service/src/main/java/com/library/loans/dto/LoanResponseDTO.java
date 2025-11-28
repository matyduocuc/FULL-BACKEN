package com.library.loans.dto;

import com.library.loans.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponseDTO {

    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Loan.Status status;
    private String statusFrontend;       // "active", "returned" - compatible con frontend React
    private String statusFrontendLegacy; // "pendiente", "aprobado", "rechazado", "devuelto" - legacy
    private Integer loanDays;
    private BigDecimal fineAmount;
    private Integer extensionsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LoanResponseDTO fromEntity(Loan loan) {
        return LoanResponseDTO.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .bookId(loan.getBookId())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .statusFrontend(mapStatusToFrontend(loan.getStatus()))
                .statusFrontendLegacy(mapStatusToFrontendLegacy(loan.getStatus()))
                .loanDays(loan.getLoanDays())
                .fineAmount(loan.getFineAmount())
                .extensionsCount(loan.getExtensionsCount())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .build();
    }

    private static String mapStatusToFrontend(Loan.Status status) {
        return switch (status) {
            case PENDING -> "pending";
            case ACTIVE, OVERDUE -> "active";
            case RETURNED -> "returned";
            case CANCELLED -> "returned"; // cancelled se trata como terminado
        };
    }

    private static String mapStatusToFrontendLegacy(Loan.Status status) {
        return switch (status) {
            case PENDING -> "pendiente";
            case ACTIVE -> "aprobado";
            case RETURNED -> "devuelto";
            case OVERDUE -> "aprobado"; // sigue activo pero vencido
            case CANCELLED -> "rechazado";
        };
    }
}






