package bysverdlov.demo.repositories;

import bysverdlov.demo.models.Payment;
import bysverdlov.demo.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByUser(User user, Pageable pageable);
}
