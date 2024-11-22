package com.group4.payment.service;

import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.exceptions.ResourceNotFoundException;
import com.group4.payment.exceptions.TimeoutException;
import com.group4.payment.mapper.PaymentMapper;
import com.group4.payment.model.Payment;
import com.group4.payment.repository.PaymentRepository;
import com.group4.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private MultipartFile multipartFile;

    private Payment payment;
    private PaymentCreateDTO paymentCreateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        payment = new Payment();
        payment.setId("test-payment-id");
        payment.setStatus("UNVERIFIED");
        payment.setNominal(1000);

        paymentCreateDTO = new PaymentCreateDTO();
        paymentCreateDTO.setNominal(1000);
    }

    @Test
    void testGetPaymentById_Success() {
        // Arrange
        when(paymentRepository.findById("test-payment-id")).thenReturn(Optional.of(payment));

        // Act
        Payment result = paymentService.getPaymentById("test-payment-id");

        // Assert
        assertThat(result).isEqualTo(payment);
        verify(paymentRepository, times(1)).findById("test-payment-id");
    }

    @Test
    void testGetPaymentById_NotFound() {
        // Arrange
        when(paymentRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> paymentService.getPaymentById("invalid-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found for this id : invalid-id");
    }

    @Test
    void testCreatePayment_Success() {
        // Arrange
        Payment mappedPayment = new Payment();
        mappedPayment.setNominal(1000);
        mappedPayment.setStatus("UNVERIFIED");

        when(paymentMapper.toPayment(paymentCreateDTO)).thenReturn(mappedPayment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mappedPayment);

        // Act
        Payment result = paymentService.createPayment(paymentCreateDTO);

        // Assert
        assertThat(result.getStatus()).isEqualTo("UNVERIFIED");
        assertThat(result.getNominal()).isEqualTo(1000);
        verify(paymentRepository, times(1)).save(mappedPayment);
    }

    @Test
    void testChangeVerifyStatus_Success() {
        // Arrange
        when(paymentRepository.findById("test-payment-id")).thenReturn(Optional.of(payment));

        // Act
        paymentService.changeVerifyStatus("test-payment-id", "VERIFIED");
        Payment check = paymentService.getPaymentById("test-payment-id");

        // Assert
        assertThat(check.getStatus()).isEqualTo("VERIFIED");
    }

    @Test
    void testChangeVerifyStatus_PaymentNotFound() {
        // Arrange
        when(paymentRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> paymentService.changeVerifyStatus("invalid-id", "VERIFIED"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found for this id : invalid-id");
    }

    @Test
    void testAddPaymentEvidence_Success() throws IOException {
        // Arrange
        when(paymentRepository.findById("test-payment-id")).thenReturn(Optional.of(payment));
        when(multipartFile.getOriginalFilename()).thenReturn("evidence.png");
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        Path tempFilePath = Files.createTempFile("evidence", ".png");

        String expectedImageUrl = tempFilePath.getFileName().toString();

        // Act
        paymentService.addPaymentEvidence("test-payment-id", multipartFile);
        Payment check = paymentService.getPaymentById("test-payment-id");

        // Assert
        assertThat(check.getPicture()).isNotNull();
        assertThat(check.getPicture()).contains("evidence.png");
        assertThat(check.getPaymentDate()).isNotNull();
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testAddPaymentEvidence_FileNotFound() {
        // Arrange
        when(paymentRepository.findById("test-payment-id")).thenReturn(Optional.of(payment));
        when(multipartFile.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> paymentService.addPaymentEvidence("test-payment-id", multipartFile))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("File not found.");
    }

    @Test
    void testSaveImage_Success() throws IOException {
        // Arrange
        when(multipartFile.getOriginalFilename()).thenReturn("evidence.png");
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        // Act
        String fileName = paymentService.saveImage(multipartFile);

        // Assert
        assertThat(fileName).contains("evidence.png");
        verify(multipartFile, times(1)).getBytes();
    }

    @Test
    void testSaveImage_Failure() throws IOException {
        // Arrange
        when(multipartFile.getOriginalFilename()).thenReturn("evidence.png");
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        // Act & Assert
        assertThatThrownBy(() -> paymentService.saveImage(multipartFile))
                .isInstanceOf(TimeoutException.class)
                .hasMessageContaining("Could not save image");
    }
}
