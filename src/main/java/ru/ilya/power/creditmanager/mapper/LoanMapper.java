package ru.ilya.power.creditmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ilya.power.creditmanager.dto.loan.CreateLoanRequest;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.entity.Loan;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "status", source = "status.name")
    LoanDto toDto(Loan loan);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "status", ignore = true)
    Loan toEntity(CreateLoanRequest request);
}