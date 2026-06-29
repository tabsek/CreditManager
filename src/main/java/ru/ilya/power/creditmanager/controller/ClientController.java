package ru.ilya.power.creditmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ilya.power.creditmanager.dto.client.ClientCardDto;
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

    @GetMapping("/{id}")
    public ResponseEntity<ClientCardDto> getClientCard(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientCard(id));
    }
}