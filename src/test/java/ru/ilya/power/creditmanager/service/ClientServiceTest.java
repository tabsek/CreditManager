package ru.ilya.power.creditmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ilya.power.creditmanager.dto.client.ClientCardDto;
import ru.ilya.power.creditmanager.exception.ClientNotFoundException;
import ru.ilya.power.creditmanager.entity.Client;
import ru.ilya.power.creditmanager.mapper.ClientMapper;
import ru.ilya.power.creditmanager.repository.ClientRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    @Test
    void getClientCard_success() {
        Client client = new Client();
        client.setId(1L);

        ClientCardDto expectedDto = new ClientCardDto();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toCardDto(client)).thenReturn(expectedDto);

        ClientCardDto result = clientService.getClientCard(1L);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void getClientCard_clientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientCard(99L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");
    }
}