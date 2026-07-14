package ru.ilya.power.creditmanager.dto.client;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateClientRequest {

    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthdate;

    @Email
    private String email;

    private Long statusId;
}