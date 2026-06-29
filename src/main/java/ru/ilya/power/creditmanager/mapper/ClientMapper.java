package ru.ilya.power.creditmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ilya.power.creditmanager.dto.client.ClientCardDto;
import ru.ilya.power.creditmanager.dto.client.ClientDto;
import ru.ilya.power.creditmanager.entity.Client;

@Mapper(componentModel = "spring", uses = {LoanMapper.class})
public interface ClientMapper {

    @Mapping(target = "status", source = "status.name")
    ClientDto toDto(Client client);

    @Mapping(target = "status", source = "status.name")
    ClientCardDto toCardDto(Client client);
}