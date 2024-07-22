package bysverdlov.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserBalanceDTO {
    @NotBlank(message = "Номер телефона не должен быть пустым")
    @Size(min = 6, max = 15, message = "Номер телефона должен содержать от 6 до 15 символов")
    @Pattern(regexp = "^\\+?[0-9. ()-]{6,15}$", message = "Неверный формат номера телефона")
    private String phoneNumber;

    private BigDecimal balance;
}
