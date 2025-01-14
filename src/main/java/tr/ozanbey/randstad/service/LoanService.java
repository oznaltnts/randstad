package tr.ozanbey.randstad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.ozanbey.randstad.domain.Loan;
import tr.ozanbey.randstad.repository.LoanRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoanInstallmentService loanInstallmentService;

    @Autowired
    private LoanRepository loanRepository;

    public Loan createLoan(Loan loan) {
        loan.setCreateDate(LocalDateTime.now());
        loan = loanRepository.save(loan);
        customerService.increaseUsedCreditLimit(loan.getCustomer().getId(), loan.getLoanAmount());
        loanInstallmentService.createInstallmentsForLoan(loan);
        return loan;
    }

    public List<Loan> getAllLoans(Long customerId) {
        return loanRepository.findAllByCustomerId(customerId);
    }

    public Loan closeLoan(Long loanId) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (optionalLoan.isPresent()) {
            optionalLoan.get().setPaid(true);
            loanRepository.save(optionalLoan.get());
            return optionalLoan.get();
        }
        return null;
    }

    public Optional<Loan> getLoanById(Long loanId) {
        return loanRepository.findById(loanId);
    }
}
