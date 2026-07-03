package ru.ilya.power.creditmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ilya.power.creditmanager.dto.client.ClientCardDto;
import ru.ilya.power.creditmanager.dto.client.ClientDto;
import ru.ilya.power.creditmanager.entity.Client;
import ru.ilya.power.creditmanager.exception.ClientNotFoundException;
import ru.ilya.power.creditmanager.mapper.ClientMapper;
import ru.ilya.power.creditmanager.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public List<ClientDto> searchByName(String lastName, String firstName) {
        return clientRepository
                .findByLastNameContainingIgnoreCaseAndFirstNameContainingIgnoreCase(
                        lastName, firstName
                )
                .stream()
                .map(clientMapper::toDto)
                .toList();
    }

    public List<ClientDto> searchByPassport(String passportNumber) {
        return clientRepository
                .findByPassportNumber(passportNumber)
                .stream()
                .map(clientMapper::toDto)
                .toList();
    }

    public ClientCardDto getClientCard(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return clientMapper.toCardDto(client);
    }

}