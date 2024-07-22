package bysverdlov.demo.services;

import bysverdlov.demo.dto.UserDTO;
import bysverdlov.demo.error.UserNotCreatedException;
import bysverdlov.demo.error.UserNotFoundException;
import bysverdlov.demo.models.User;
import bysverdlov.demo.repositories.UserRepository;
import bysverdlov.demo.util.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private  ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user;

    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setPhoneNumber("1234567890");
        user.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setPhoneNumber("1234567890");
        userDTO.setPassword("password");
    }

    @Test
    public void testRegisterUser_Success() {
        when(userRepository.findByPhoneNumber(userDTO.getPhoneNumber())).thenReturn(null);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);

        userService.registerUser(userDTO);

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(BigDecimal.valueOf(1000.0), user.getBalance());
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        when(userRepository.findByPhoneNumber(userDTO.getPhoneNumber())).thenReturn(user);

        UserNotCreatedException exception = assertThrows(UserNotCreatedException.class, () -> {
            userService.registerUser(userDTO);
        });

        assertEquals("Пользователь с номером телефона " + user.getPhoneNumber() + " уже зарегистрирован", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Ivan");
        userDTO.setLastName("Ivanov");
        userDTO.setEmail("ivanov@gmail.com");
        userDTO.setGender(Gender.MALE);
        userDTO.setBirthDate(LocalDate.of(1995, 2, 28));

        when(userRepository.findByPhoneNumber(user.getPhoneNumber())).thenReturn(user);

        userService.updateUserPatch(user.getPhoneNumber(), userDTO);

        verify(userRepository, times(1)).save(user);
        assertEquals("Ivan", user.getFirstName());
        assertEquals("Ivanov", user.getLastName());
        assertEquals("ivanov@gmail.com", user.getEmail());
        assertEquals(Gender.MALE, user.getGender());
        assertEquals(LocalDate.of(1995, 2, 28), user.getBirthDate());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        String phoneNumber = "1234567890";
        UserDTO userDTO = new UserDTO();

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(null);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserPatch(phoneNumber, userDTO);
        });

        assertEquals("Пользователь с данным номером телефона не зарегистрирован", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
