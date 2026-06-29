package ru.ilya.power.creditmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.entity.Loan;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "status", source = "status.name")
    LoanDto toDto(Loan loan);
}