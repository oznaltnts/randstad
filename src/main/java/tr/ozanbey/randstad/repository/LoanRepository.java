package tr.ozanbey.randstad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.ozanbey.randstad.domain.Loan;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByCustomerId(Long customerId);
}
