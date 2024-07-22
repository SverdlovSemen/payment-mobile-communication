package bysverdlov.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    @NotBlank(message = "Номер телефона должен быть указан")
    @Size(min = 6, max = 15, message = "Номер телефона должен содержать от 6 до 15 символов")
    @Pattern(regexp = "^\\+?[0-9. ()-]{6,15}$", message = "Неверный формат номера телефона")
    private String phoneNumber;

    private BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime date;
}
