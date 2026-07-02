package ru.ilya.power.creditmanager.dto.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class UpdateLoanRequest {

    private BigInteger amount;
    private BigDecimal interestRate;
    private String status;
}