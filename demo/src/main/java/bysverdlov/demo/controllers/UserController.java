package bysverdlov.demo.controllers;

import bysverdlov.demo.dto.PaymentDTO;
import bysverdlov.demo.dto.UserBalanceDTO;
import bysverdlov.demo.dto.UserDTO;
import bysverdlov.demo.services.PaymentService;
import bysverdlov.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PaymentService paymentService;

    @Autowired
    public UserController(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody @Valid UserDTO userDTO){
        userService.registerUser(userDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<UserBalanceDTO> getBalance(@AuthenticationPrincipal UserDetails userDetails){
        UserBalanceDTO userBalanceDTO = userService.getUserBalanceByPhoneNumber(userDetails.getUsername());
        return ResponseEntity.ok(userBalanceDTO);
    }

    @GetMapping("/payments")
    public ResponseEntity<Page<PaymentDTO>> getPayments(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        String phoneNumber = userDetails.getUsername();
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDTO> payments = paymentService.getPaymentsByUser(phoneNumber, pageable);
        return ResponseEntity.ok(payments);
    }


    @PatchMapping("/update")
    public ResponseEntity<HttpStatus> updateUserPatch(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody @Valid UserDTO userDTO) {
        String phoneNumber = userDetails.getUsername();
        userService.updateUserPatch(phoneNumber, userDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<HttpStatus> updateUserPut(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody @Valid UserDTO userDTO) {
        String phoneNumber = userDetails.getUsername();
        userService.updateUserPut(phoneNumber, userDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}


























