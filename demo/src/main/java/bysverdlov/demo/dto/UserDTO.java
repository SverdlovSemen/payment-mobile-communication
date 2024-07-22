package bysverdlov.demo.dto;

import bysverdlov.demo.util.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserDTO {
    @NotBlank(message = "Номер телефона не должен быть пустым")
    @Size(min = 6, max = 15, message = "Номер телефона должен содержать от 6 до 15 символов")
    @Pattern(regexp = "^\\+?[0-9. ()-]{6,15}$", message = "Неверный формат номера телефона")
    private String phoneNumber;

    @NotBlank(message = "Пароль не должен быть пустым")
    private String password;

    private BigDecimal balance = BigDecimal.ZERO;

    private String firstName;

    private String lastName;

    @Email(message = "Неверный формат email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}
