package bysverdlov.demo.services;

import bysverdlov.demo.error.UserNotFoundException;
import bysverdlov.demo.models.Payment;
import bysverdlov.demo.models.User;
import bysverdlov.demo.repositories.PaymentRepository;
import bysverdlov.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private User userPayer;
    private User userRecipient;

    @BeforeEach
    public void setUp() {
        userPayer = new User();
        userPayer.setPhoneNumber("1234567890");

        userRecipient = new User();
        userRecipient.setPhoneNumber("0987654321");
    }

    @Test
    public void testProcessPayment_Success() {
        userPayer.setBalance(BigDecimal.valueOf(2000.0));

        userRecipient.setBalance(BigDecimal.valueOf(1000.0));

        BigDecimal amount = BigDecimal.valueOf(500.0);

        when(userRepository.findByPhoneNumberForUpdate("1234567890")).thenReturn(userPayer);
        when(userRepository.findByPhoneNumberForUpdate("0987654321")).thenReturn(userRecipient);

        String result = paymentService.processPayment("1234567890", "0987654321", amount);

        verify(userRepository, times(1)).save(userPayer);
        verify(userRepository, times(1)).save(userRecipient);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        assertEquals("Платеж прошел успешно", result);
        assertEquals(BigDecimal.valueOf(1500.0), userPayer.getBalance());
        assertEquals(BigDecimal.valueOf(1500.0), userRecipient.getBalance());
    }

    @Test
    public void testProcessPayment_UserPayerNotFound() {
        when(userRepository.findByPhoneNumberForUpdate("1234567890")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> paymentService.processPayment("1234567890", "0987654321", BigDecimal.valueOf(500.0)));
    }

    @Test
    public void testProcessPayment_UserRecipientNotFound() {
        when(userRepository.findByPhoneNumberForUpdate("1234567890")).thenReturn(userPayer);
        when(userRepository.findByPhoneNumberForUpdate("0987654321")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> paymentService.processPayment("1234567890", "0987654321", BigDecimal.valueOf(500.0)));
    }

    @Test
    public void testProcessPayment_InsufficientBalance() {
        userPayer.setBalance(BigDecimal.valueOf(200.0));

        BigDecimal amount = BigDecimal.valueOf(500.0);

        when(userRepository.findByPhoneNumberForUpdate("1234567890")).thenReturn(userPayer);
        when(userRepository.findByPhoneNumberForUpdate("0987654321")).thenReturn(userRecipient);

        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment("1234567890", "0987654321", amount));
    }

    @Test
    public void testProcessPayment_NonPositiveAmount() {
        when(userRepository.findByPhoneNumberForUpdate("1234567890")).thenReturn(userPayer);
        when(userRepository.findByPhoneNumberForUpdate("0987654321")).thenReturn(userRecipient);

        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment("1234567890", "0987654321", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment("1234567890", "0987654321", BigDecimal.valueOf(-100.0)));
    }
}