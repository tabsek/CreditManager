package ru.ilya.power.creditmanager.dto.loan;

import lombok.Getter;
import lombok.Setter;
import java.math.BigInteger;

@Getter
@Setter
public class LoanDto {

    private Long id;
    private String loanNumber;
    private String currency;
    private BigInteger amount;
    private String interestRate;
    private Integer termMonths;
    private String monthlyPayment;
    private String status;
}