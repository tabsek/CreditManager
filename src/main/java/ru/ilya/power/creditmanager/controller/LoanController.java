package ru.ilya.power.creditmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ilya.power.creditmanager.dto.loan.CreateLoanRequest;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.dto.loan.UpdateLoanRequest;
import ru.ilya.power.creditmanager.service.LoanService;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/clients/{clientId}")
    public ResponseEntity<LoanDto> createLoan(
            @PathVariable Long clientId,
            @Valid @RequestBody CreateLoanRequest request
            ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(loanService.createLoan(clientId, request));
    }

    @PatchMapping("/{loanId}")
    public ResponseEntity<LoanDto> updateLoan(
            @PathVariable Long loanId,
            @RequestBody UpdateLoanRequest request
    ) {
        return ResponseEntity.ok(loanService.updateLoan(loanId, request));
    }
}
