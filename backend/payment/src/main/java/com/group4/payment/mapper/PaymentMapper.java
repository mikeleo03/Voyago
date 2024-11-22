package com.group4.payment.mapper;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.dto.PaymentUpdateDTO;
import com.group4.payment.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "UNVERIFIED")
    @Mapping(target = "paymentDate", ignore = true)
    Payment toPayment(PaymentCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Payment toPayment(PaymentUpdateDTO dto);

    PaymentCreateDTO toPaymentCreateDTO(Payment payment);

    PaymentUpdateDTO toPaymentUpdateDTO(Payment payment);
}
