package ru.ilya.power.creditmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ilya.power.creditmanager.dto.client.ClientCardDto;
import ru.ilya.power.creditmanager.dto.client.ClientDto;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.entity.Client;
import ru.ilya.power.creditmanager.exception.ClientNotFoundException;
import ru.ilya.power.creditmanager.mapper.ClientMapper;
import ru.ilya.power.creditmanager.mapper.LoanMapper;
import ru.ilya.power.creditmanager.repository.ClientRepository;
import ru.ilya.power.creditmanager.repository.LoanRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
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

        ClientCardDto dto = clientMapper.toCardDto(client);
        List<LoanDto> loans = loanRepository.findByClientId(id)
                .stream()
                .map(loanMapper::toDto)
                .toList();
        dto.setLoans(loans);
        return dto;
    }

}