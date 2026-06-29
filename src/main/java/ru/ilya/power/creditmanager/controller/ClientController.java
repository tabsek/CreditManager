package ru.ilya.power.creditmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ilya.power.creditmanager.dto.client.ClientDto;
import ru.ilya.power.creditmanager.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientDto>> searchClients(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String passportNumber
    ) {
        if (passportNumber != null) {
            return ResponseEntity.ok(clientService.searchByPassport(passportNumber));
        }
        return ResponseEntity.ok(clientService.searchByName(
                lastName != null ? lastName : "",
                firstName != null ? firstName : ""
        ));
    }
}