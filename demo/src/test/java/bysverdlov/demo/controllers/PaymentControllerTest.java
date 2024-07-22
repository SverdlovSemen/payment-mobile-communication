package bysverdlov.demo.controllers;

import bysverdlov.demo.dto.PaymentProcessDTO;
import bysverdlov.demo.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;

    @Test
    public void testProcessPayment() throws Exception {
        String phoneNumberPayer = "testUser";
        String phoneNumberRecipient = "recipientUser";
        BigDecimal amount = BigDecimal.valueOf(100);

        when(paymentService.processPayment(anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn("Success");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(phoneNumberPayer);

        ResponseEntity<String> responseEntity = paymentController.processPayment(
                userDetails,
                new PaymentProcessDTO(phoneNumberRecipient, amount));

        verify(paymentService, times(1)).processPayment(phoneNumberPayer, phoneNumberRecipient, amount);
        assertEquals("Success", responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());
    }
}