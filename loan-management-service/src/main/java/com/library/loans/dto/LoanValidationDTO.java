package com.library.loans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanValidationDTO {

    private Long userId;
    private Long bookId;
    private Boolean valid;
    private String message;
    private Boolean userExists;
    private Boolean bookAvailable;
}


