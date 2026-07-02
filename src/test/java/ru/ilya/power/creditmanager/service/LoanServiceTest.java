package ru.ilya.power.creditmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ilya.power.creditmanager.dto.loan.CreateLoanRequest;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.entity.*;
import ru.ilya.power.creditmanager.exception.ClientNotActiveException;
import ru.ilya.power.creditmanager.exception.ClientNotFoundException;
import ru.ilya.power.creditmanager.exception.DuplicateLoanNumberException;
import ru.ilya.power.creditmanager.mapper.LoanMapper;
import ru.ilya.power.creditmanager.repository.ClientRepository;
import ru.ilya.power.creditmanager.repository.CurrencyRepository;
import ru.ilya.power.creditmanager.repository.LoanRepository;
import ru.ilya.power.creditmanager.repository.LoanStatusRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private LoanStatusRepository loanStatusRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    @Test
    void createLoan_success() {
        Client client = new Client();
        ClientStatus activeStatus = new ClientStatus();
        activeStatus.setName("ACTIVE");
        client.setId(1L);
        client.setStatus(activeStatus);

        CreateLoanRequest request = new CreateLoanRequest();
        request.setLoanNumber("TT-TEST-001");
        request.setCurrency("RUB");
        request.setAmount(BigInteger.valueOf(100000));
        request.setInterestRate(BigDecimal.valueOf(12.5));
        request.setTermMonths(12);
        request.setMonthlyPayment(BigDecimal.valueOf(9000));

        Currency currency = new Currency();
        currency.setCode("RUB");

        LoanStatus draftStatus = new LoanStatus();
        draftStatus.setName("DRAFT");

        Loan savedLoan = new Loan();
        LoanDto expectedDto = new LoanDto();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(loanRepository.existsByLoanNumber("TT-TEST-001")).thenReturn(false);
        when(currencyRepository.findByCode("RUB")).thenReturn(Optional.of(currency));
        when(loanStatusRepository.findByName("DRAFT")).thenReturn(Optional.of(draftStatus));
        when(loanMapper.toEntity(request)).thenReturn(savedLoan);
        when(loanRepository.save(any())).thenReturn(savedLoan);
        when(loanMapper.toDto(savedLoan)).thenReturn(expectedDto);

        LoanDto result = loanService.createLoan(1L, request);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void createLoan_clientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.createLoan(99L, new CreateLoanRequest()))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createLoan_clientNotActive() {
        Client client = new Client();
        ClientStatus blockedStatus = new ClientStatus();
        blockedStatus.setName("BLOCKED");
        client.setId(1L);
        client.setStatus(blockedStatus);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> loanService.createLoan(1L, new CreateLoanRequest()))
                .isInstanceOf(ClientNotActiveException.class)
                .hasMessageContaining("1");
    }

    @Test
    void createLoan_duplicateLoanNumber() {
        Client client = new Client();
        ClientStatus activeStatus = new ClientStatus();
        activeStatus.setName("ACTIVE");
        client.setStatus(activeStatus);

        CreateLoanRequest request = new CreateLoanRequest();
        request.setLoanNumber("LN-2024-001");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(loanRepository.existsByLoanNumber("LN-2024-001")).thenReturn(true);

        assertThatThrownBy(() -> loanService.createLoan(1L, request))
                .isInstanceOf(DuplicateLoanNumberException.class)
                .hasMessageContaining("LN-2024-001");
    }

}