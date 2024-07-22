package bysverdlov.demo.services;

import bysverdlov.demo.dto.PaymentDTO;
import bysverdlov.demo.error.UserNotFoundException;
import bysverdlov.demo.models.Payment;
import bysverdlov.demo.models.User;
import bysverdlov.demo.repositories.PaymentRepository;
import bysverdlov.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(UserRepository userRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String processPayment(String phoneNumberPayer, String phoneNumberRecipient , BigDecimal amount) {
        User userPayer = userRepository.findByPhoneNumberForUpdate(phoneNumberPayer);
        User userRecipient = userRepository.findByPhoneNumberForUpdate(phoneNumberRecipient);

        validateUsers(userPayer, userRecipient, phoneNumberPayer, phoneNumberRecipient);
        validatePaymentRequest(userPayer, amount);

        executePayment(userPayer, userRecipient, amount);
        return "Платеж прошел успешно";
    }

    private void validateUsers(User userPayer, User userRecipient, String phoneNumberPayer, String phoneNumberRecipient) {
        if (userPayer == null) {
            throw new UserNotFoundException("Пользователь с номером телефона " + phoneNumberPayer + " не найден");
        }
        if (userRecipient == null) {
            throw new UserNotFoundException("Пользователь с номером телефона " + phoneNumberRecipient + " не найден");
        }
        if (userPayer.equals(userRecipient)) {
            throw new IllegalArgumentException("Оплата не прошла. Вы ввели собственный номер телефона");
        }
    }

    private void validatePaymentRequest(User userPayer, BigDecimal amount) {
        if (userPayer.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Оплата не прошла. Недостаточно средств");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма платежа должна быть больше 0");
        }
    }

    private void executePayment(User userPayer, User userRecipient, BigDecimal  amount) {
        userPayer.setBalance(userPayer.getBalance().subtract(amount));
        userRecipient.setBalance(userRecipient.getBalance().add(amount));

        userRepository.save(userPayer);
        userRepository.save(userRecipient);

        Payment payment = new Payment();
        payment.setUser(userPayer);
        payment.setPhoneNumber(userRecipient.getPhoneNumber());
        payment.setAmount(amount);
        payment.setDate(LocalDateTime.now());

        paymentRepository.save(payment);
    }

    public Page<PaymentDTO> getPaymentsByUser(String phoneNumber, Pageable pageable){
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if(user == null){
            throw new UserNotFoundException("Пользователь с данным номером телефона не зарегистрирован");
        }
        Page<Payment> payments = paymentRepository.findByUser(user, pageable);
        return payments.map(this::convertToDTO);
    }

    private PaymentDTO convertToDTO(Payment payment){
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setPhoneNumber(payment.getPhoneNumber());
        dto.setAmount(payment.getAmount());
        dto.setDate(payment.getDate());
        return dto;
    }
}






















