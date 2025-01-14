package tr.ozanbey.randstad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.ozanbey.randstad.domain.LoanInstallment;

import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    List<LoanInstallment> findAllByLoanIdOrderByDueDate(Long loanId);

    List<LoanInstallment> findAllByLoanIdAndIsPaidOrderByDueDate(Long loanId, boolean paid);

}
