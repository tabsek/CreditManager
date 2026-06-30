package ru.ilya.power.creditmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ilya.power.creditmanager.dto.client.ClientCardDto;
import ru.ilya.power.creditmanager.dto.client.ClientDto;
import ru.ilya.power.creditmanager.exception.ValidationException;
import ru.ilya.power.creditmanager.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientDto>> searchClients(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String passportNumber
    ) {
        boolean hasFirsLastNames = lastName != null || firstName != null;
        boolean hasPassport = passportNumber != null;

        if (hasFirsLastNames && hasPassport) {
            throw new ValidationException(
                    "Cannot search by both last name first name and passport number simultaneously"
            );
        }

        if (hasPassport) {
            return ResponseEntity.ok(clientService.searchByPassport(passportNumber));
        }

        if (hasFirsLastNames) {
            if (lastName == null || firstName == null) {
                throw new ValidationException(
                        "Both lastName and firstName must be provided for name search"
                );
            }
            return ResponseEntity.ok(clientService.searchByName(lastName, firstName));
        }

        throw new ValidationException(
                "Either lastName and firstName, or passportNumber must be provided"
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientCardDto> getClientCard(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientCard(id));
    }
}