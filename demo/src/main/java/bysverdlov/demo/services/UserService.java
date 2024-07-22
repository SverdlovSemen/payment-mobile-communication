package bysverdlov.demo.services;

import bysverdlov.demo.dto.UserBalanceDTO;
import bysverdlov.demo.dto.UserDTO;
import bysverdlov.demo.error.EmailNotValidException;
import bysverdlov.demo.error.UserNotCreatedException;
import bysverdlov.demo.error.UserNotFoundException;
import bysverdlov.demo.models.User;
import bysverdlov.demo.repositories.UserRepository;
import bysverdlov.demo.validator.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, ModelMapper modelMapper){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public void registerUser(UserDTO userDTO) {
        if (getUserByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new UserNotCreatedException("Пользователь с номером телефона " + userDTO.getPhoneNumber() + " уже зарегистрирован");
        }
        validateEmail(userDTO.getEmail());

        User user = convertToUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setBalance(BigDecimal.valueOf(1000.0));
        userRepository.save(user);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public UserBalanceDTO getUserBalanceByPhoneNumber(String phoneNumber){
        User user = getUserByPhoneNumber(phoneNumber);
        return new UserBalanceDTO(user.getPhoneNumber(), user.getBalance());
    }

    public void updateUserPatch(String phoneNumber, UserDTO userDTO) {
        User user = checkIsPhoneNumberExist(phoneNumber);

        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }

        validateEmail(userDTO.getEmail());

        if(userDTO.getEmail() != null && EmailValidator.validate(userDTO.getEmail())){
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getGender() != null) {
            user.setGender(userDTO.getGender());
        }
        if (userDTO.getBirthDate() != null) {
            user.setBirthDate(userDTO.getBirthDate());
        }
        saveUser(user);
    }

    public void updateUserPut(String phoneNumber, UserDTO userDTO) {
        User user = checkIsPhoneNumberExist(phoneNumber);

        validateEmail(userDTO.getEmail());

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setBirthDate(userDTO.getBirthDate());

        saveUser(user);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    private User convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }

    private void validateEmail(String email){
        if (email != null && !EmailValidator.validate(email)) {
            throw new EmailNotValidException("Не валидный email. Измените email и попробуйте ещё раз");
        }
    }

    private User checkIsPhoneNumberExist(String phoneNumber){
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if(user == null){
            throw new UserNotFoundException("Пользователь с данным номером телефона не зарегистрирован");
        }
        return user;
    }
}


















