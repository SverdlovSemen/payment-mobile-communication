package bysverdlov.demo.controllers;

import bysverdlov.demo.dto.PaymentDTO;
import bysverdlov.demo.dto.UserDTO;
import bysverdlov.demo.error.UserNotCreatedException;
import bysverdlov.demo.error.UserNotFoundException;
import bysverdlov.demo.models.User;
import bysverdlov.demo.services.CustomUserDetailsService;
import bysverdlov.demo.services.PaymentService;
import bysverdlov.demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setPhoneNumber("1234567890");
        user.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setPhoneNumber("987654321");
        userDTO.setPassword("pass");

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testRegister_Success() {
        ResponseEntity<HttpStatus> responseEntity = userController.register(userDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userService, times(1)).registerUser(userDTO);
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        doThrow(new UserNotCreatedException("Пользователь с номером телефона " + userDTO.getPhoneNumber() + " уже зарегистрирован"))
                .when(userService).registerUser(userDTO);

        assertThrows(UserNotCreatedException.class, () -> userController.register(userDTO));
    }

    @Test
    public void testUpdateUser() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("1234567890");

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Petr");
        userDTO.setLastName("Petrov");

        ResponseEntity<HttpStatus> response = userController.updateUserPatch(userDetails, userDTO);

        Mockito.verify(userService).updateUserPatch("1234567890", userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getPayments_ReturnsPageOfPaymentDTOs() {
        UserDetails userDetails = mock(UserDetails.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaymentDTO> expectedPayments = mock(Page.class);
        when(userDetails.getUsername()).thenReturn("1234567890");
        when(paymentService.getPaymentsByUser("1234567890", pageable)).thenReturn(expectedPayments);

        ResponseEntity<Page<PaymentDTO>> response = userController.getPayments(userDetails, 0, 10);

        assertEquals(expectedPayments, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService, times(1)).getPaymentsByUser("1234567890", pageable);
    }

    @Test
    public void testGetPaymentsByUser_UserNotFoundException() {
        String phoneNumber = "testUser";
        Pageable pageable = PageRequest.of(0, 10);
        when(paymentService.getPaymentsByUser(phoneNumber, pageable))
                .thenThrow(new UserNotFoundException("Пользователь с данным номером телефона не зарегистрирован"));

        UserDetails userDetails = withUsername("testUser")
                .password("password")
                .roles("USER")
                .build();

        assertThrows(UserNotFoundException.class, () -> userController.getPayments(userDetails, 0, 10));
    }
}
