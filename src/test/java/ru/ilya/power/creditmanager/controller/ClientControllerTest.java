package ru.ilya.power.creditmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ilya.power.creditmanager.dto.client.ClientDto;
import ru.ilya.power.creditmanager.service.ClientService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Test
    void searchClients_byPassport_success() throws Exception {
        ClientDto dto = new ClientDto();
        dto.setId(1L);

        when(clientService.searchByPassport("4510123456"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/clients")
                        .param("passportNumber", "4510123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void searchClients_byName_success() throws Exception {
        ClientDto dto = new ClientDto();
        dto.setId(2L);

        when(clientService.searchByName("Петров", "Иван"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/clients")
                        .param("lastName", "Петров")
                        .param("firstName", "Иван"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void searchClients_noParams_badRequest() throws Exception {
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Either lastName and firstName, or passportNumber must be provided"
                ));
    }

    @Test
    void searchClients_onlyLastName_badRequest() throws Exception {
        mockMvc.perform(get("/api/clients")
                        .param("lastName", "Петров"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Both lastName and firstName must be provided for name search"
                ));
    }

    @Test
    void searchClients_nameAndPassport_badRequest() throws Exception {
        mockMvc.perform(get("/api/clients")
                        .param("lastName", "Петров")
                        .param("firstName", "Иван")
                        .param("passportNumber", "4510123456"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Cannot search by both last name first name and passport number simultaneously"
                ));
    }
}