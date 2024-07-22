package bysverdlov.demo.controllers;

import bysverdlov.demo.dto.PaymentProcessDTO;
import bysverdlov.demo.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PaymentProcessDTO paymentProcessDTO) {

        String phoneNumberPayer = userDetails.getUsername();
        String result = paymentService.processPayment(phoneNumberPayer, paymentProcessDTO.getPhoneNumber(), paymentProcessDTO.getAmount());

        return ResponseEntity.ok(result);
    }
}
























