package ru.ilya.power.creditmanager.dto.client;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClientDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthdate;
    private String passportNumber;
    private String email;
    private String status;
}