package bysverdlov.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "Payment")
public class Payment {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "date")
    private LocalDateTime date;

    @NotBlank(message = "Номер телефона должен быть указан")
    @Pattern(regexp = "^\\+?[0-9. ()-]{6,15}$", message = "Неверный формат номера телефона")
    @Size(min = 6, max = 15, message = "Номер телефона должен содержать от 6 до 15 символов")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull(message = "Сумма платежа должна быть указана")
    @Positive(message = "Сумма должна быть больше 0")
    @Column(name = "amount")
    private BigDecimal amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return getId() != null && Objects.equals(getId(), payment.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
