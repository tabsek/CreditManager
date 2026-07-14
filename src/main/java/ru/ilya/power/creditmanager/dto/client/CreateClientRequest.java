package ru.ilya.power.creditmanager.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateClientRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String middleName;

    @NotNull
    private LocalDate birthdate;

    @NotBlank
    private String passportNumber;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Long statusId;
}