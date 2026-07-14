package ru.ilya.power.creditmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ilya.power.creditmanager.dto.client.ClientCardDto;
import ru.ilya.power.creditmanager.dto.client.ClientDto;
import ru.ilya.power.creditmanager.dto.client.CreateClientRequest;
import ru.ilya.power.creditmanager.dto.client.UpdateClientRequest;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.entity.Client;
import ru.ilya.power.creditmanager.entity.ClientStatus;
import ru.ilya.power.creditmanager.exception.*;
import ru.ilya.power.creditmanager.mapper.ClientMapper;
import ru.ilya.power.creditmanager.mapper.LoanMapper;
import ru.ilya.power.creditmanager.repository.ClientRepository;
import ru.ilya.power.creditmanager.repository.ClientStatusRepository;
import ru.ilya.power.creditmanager.repository.LoanRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientStatusRepository clientStatusRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final ClientMapper clientMapper;

    private static final Long BLOCKED_CLIENT_STATUS_ID = 2L;
    private static final Long CLOSED_LOAN_STATUS_ID = 3L;

    @Transactional(readOnly = true)
    public List<ClientDto> searchByName(String lastName, String firstName) {
        return clientRepository
                .findByLastNameContainingIgnoreCaseAndFirstNameContainingIgnoreCase(
                        lastName, firstName
                )
                .stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClientDto> searchByPassport(String passportNumber) {
        return clientRepository
                .findByPassportNumber(passportNumber)
                .stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public ClientDto createClient(CreateClientRequest request) {
        if (clientRepository.existsByPassportNumber(request.getPassportNumber())) {
            throw new DuplicatePassportNumberException(request.getPassportNumber());
        }

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        ClientStatus status = clientStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new ClientStatusNotFoundException(request.getStatusId()));

        Client client = clientMapper.toEntity(request);
        client.setStatus(status);

        return clientMapper.toDto(clientRepository.save(client));
    }

    @Transactional
    public ClientDto updateClient(Long id, UpdateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (request.getFirstName() != null) {
            client.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            client.setLastName(request.getLastName());
        }
        if (request.getMiddleName() != null) {
            client.setMiddleName(request.getMiddleName());
        }
        if (request.getBirthdate() != null) {
            client.setBirthdate(request.getBirthdate());
        }
        if (request.getEmail() != null) {
            if (clientRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }
            client.setEmail(request.getEmail());
        }
        if (request.getStatusId() != null) {
            ClientStatus status = clientStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new ClientStatusNotFoundException(request.getStatusId()));

            if (BLOCKED_CLIENT_STATUS_ID.equals(request.getStatusId())
                    && loanRepository.existsByClientIdAndStatusIdNot(id, CLOSED_LOAN_STATUS_ID)) {
                throw new ClientHasActiveLoansException(id);
            }

            client.setStatus(status);
        }

        return clientMapper.toDto(clientRepository.save(client));
    }

}