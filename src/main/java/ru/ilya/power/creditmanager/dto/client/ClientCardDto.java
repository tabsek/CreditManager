package ru.ilya.power.creditmanager.dto.client;

import lombok.Getter;
import lombok.Setter;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ClientCardDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthdate;
    private String passportNumber;
    private String email;
    private String status;
    private List<LoanDto> loans;
}