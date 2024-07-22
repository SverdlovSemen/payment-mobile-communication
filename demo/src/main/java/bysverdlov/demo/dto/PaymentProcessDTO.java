package bysverdlov.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProcessDTO {
    @NotBlank(message = "Номер телефона должен быть указан")
    @Pattern(regexp = "^\\+?[0-9. ()-]{6,15}$", message = "Неверный формат номера телефона")
    @Size(min = 6, max = 15, message = "Номер телефона должен содержать от 6 до 15 символов")
    private String phoneNumber;

    @NotNull(message = "Сумма должна быть указана")
    @Positive(message = "Сумма должна быть больше 0")
    private BigDecimal amount;
}
