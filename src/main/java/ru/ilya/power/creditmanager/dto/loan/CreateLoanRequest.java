package ru.ilya.power.creditmanager.dto.loan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class CreateLoanRequest {

    @NotBlank
    private String loanNumber;

    @NotBlank
    private String currency;

    @NotNull
    @Positive
    private BigInteger amount;

    @NotNull
    @Positive
    private BigDecimal interestRate;

    @NotNull
    @Positive
    private Integer termMonths;

    @NotNull
    @Positive
    private BigDecimal monthlyPayment;
}