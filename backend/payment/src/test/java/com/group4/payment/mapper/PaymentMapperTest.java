package com.group4.payment.mapper;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.dto.PaymentUpdateDTO;
import com.group4.payment.model.Payment;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void testToPaymentFromCreateDTO() {
        PaymentCreateDTO createDTO = new PaymentCreateDTO();
        createDTO.setNominal(100);

        Payment payment = paymentMapper.toPayment(createDTO);

        assertThat(payment).isNotNull();
        assertThat(payment.getNominal()).isEqualTo(100);
        assertThat(payment.getStatus()).isEqualTo("UNVERIFIED");
        assertThat(payment.getId()).isNull();
        assertThat(payment.getPaymentDate()).isNull();
    }

    @Test
    void testToPaymentFromUpdateDTO() {
        PaymentUpdateDTO updateDTO = new PaymentUpdateDTO();
        updateDTO.setStatus("COMPLETED");
        updateDTO.setPicture("payment.png");
        updateDTO.setPaymentDate(LocalDateTime.now());

        Payment payment = paymentMapper.toPayment(updateDTO);

        assertThat(payment).isNotNull();
        assertThat(payment.getStatus()).isEqualTo("COMPLETED");
        assertThat(payment.getPicture()).isEqualTo("payment.png");
        assertThat(payment.getPaymentDate()).isEqualToIgnoringSeconds(updateDTO.getPaymentDate());
        assertThat(payment.getId()).isNull();
        assertThat(payment.getCreatedAt()).isNull();
        assertThat(payment.getUpdatedAt()).isNull();
        assertThat(payment.getCreatedBy()).isNull();
        assertThat(payment.getUpdatedBy()).isNull();
    }

    @Test
    void testToPaymentCreateDTO() {
        Payment payment = new Payment();
        payment.setId("payment-id");
        payment.setNominal(200);
        payment.setStatus("UNVERIFIED");

        PaymentCreateDTO createDTO = paymentMapper.toPaymentCreateDTO(payment);

        assertThat(createDTO).isNotNull();
        assertThat(createDTO.getNominal()).isEqualTo(200);
    }

    @Test
    void testToPaymentUpdateDTO() {
        Payment payment = new Payment();
        payment.setId("payment-id");
        payment.setPicture("payment.png");
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());

        PaymentUpdateDTO updateDTO = paymentMapper.toPaymentUpdateDTO(payment);

        assertThat(updateDTO).isNotNull();
        assertThat(updateDTO.getStatus()).isEqualTo("COMPLETED");
        assertThat(updateDTO.getPicture()).isEqualTo("payment.png");
    }
}
