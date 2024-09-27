package com.group4.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.payment.dto.PaymentCreateDTO;
import com.group4.payment.model.Payment;
import com.group4.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testAddPaymentEvidence() throws Exception {
        String paymentId = "somePaymentId";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );

        when(paymentService.addPaymentEvidence(eq(paymentId), any(MultipartFile.class)))
                .thenReturn(new Payment(paymentId, 1000, "image.jpg", LocalDateTime.now(), "UNVERIFIED"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/api/v1/payment/payment/{id}", paymentId)
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.picture").value("image.jpg"))
                .andDo(print());

        verify(paymentService, times(1)).addPaymentEvidence(eq(paymentId), any(MultipartFile.class));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testChangeVerifyStatus() throws Exception {
        String paymentId = UUID.randomUUID().toString();
        String status = "VERIFIED";
        Payment payment = new Payment(paymentId, 1000, "receipt.jpg", LocalDateTime.now(), "VERIFIED");

        when(paymentService.changeVerifyStatus(paymentId, status)).thenReturn(payment);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/payment/verify/{id}", paymentId)
                        .param("status", status))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("VERIFIED"))
                .andDo(print());

        verify(paymentService, times(1)).changeVerifyStatus(paymentId, status);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "CUSTOMER"})
    void testGetPaymentImageSuccess() throws Exception {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(paymentId, 1000, "receipt.jpg", LocalDateTime.now(), "UNVERIFIED");

        Path imagePath = Paths.get("src/main/resources/static/assets/receipt.jpg");
        Files.createFile(imagePath);

        when(paymentService.getPaymentById(paymentId)).thenReturn(payment);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/{id}/image", paymentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        verify(paymentService, times(1)).getPaymentById(paymentId);

        Files.deleteIfExists(imagePath);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "CUSTOMER"})
    void testGetPaymentImageNotFound() throws Exception {
        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(paymentId, 1000, "non_existent_image.jpg", LocalDateTime.now(), "UNVERIFIED");

        when(paymentService.getPaymentById(paymentId)).thenReturn(payment);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/{id}/image", paymentId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());

        verify(paymentService, times(1)).getPaymentById(paymentId);
    }

    @Test
    void testCreatePayment() throws Exception {
        PaymentCreateDTO dto = new PaymentCreateDTO();
        dto.setNominal(1000);

        Payment payment = new Payment(UUID.randomUUID().toString(), 1000, null, null, "UNVERIFIED");

        when(paymentService.createPayment(any(PaymentCreateDTO.class))).thenReturn(payment);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nominal").value(1000))
                .andDo(print());

        verify(paymentService, times(1)).createPayment(any(PaymentCreateDTO.class));
    }
}