package tr.ozanbey.randstad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.ozanbey.randstad.domain.Loan;
import tr.ozanbey.randstad.domain.LoanInstallment;
import tr.ozanbey.randstad.model.PaymentInformation;
import tr.ozanbey.randstad.repository.LoanInstallmentRepository;
import tr.ozanbey.randstad.repository.LoanRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanInstallmentService {

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerService customerService;

    public List<LoanInstallment> getAllInstallments(Long loanId) {
        return loanInstallmentRepository.findAllByLoanIdOrderByDueDate(loanId);
    }

    //Earliest installment should be paid first and if there are more money then you should continue to next installment.
    public PaymentInformation payInstallmentByLoanIdAndAmount(Long loanId, BigDecimal amount) {
        //Endpoint can pay multiple installments with respect to amount sent with some restriction described below
        List<LoanInstallment> unpaidLoanInstallmentList = loanInstallmentRepository.findAllByLoanIdAndIsPaidOrderByDueDate(loanId, false);
        List<LoanInstallment> paidLoanInstallmentList = new ArrayList<>();
        BigDecimal totalAmountSpent = BigDecimal.ZERO;
        BigDecimal remainingAmount = amount;
        if (unpaidLoanInstallmentList != null && !unpaidLoanInstallmentList.isEmpty()) {
            for (LoanInstallment loanInstallment : unpaidLoanInstallmentList) {
                //Installments should be paid wholly or not at all.
                // So if installments amount is 10, and you send 20,
                // 2 installments can be paid. If you send 15,
                // only 1 can be paid.
                // If you send 5,
                // no installments can be paid.
                if (remainingAmount.compareTo(loanInstallment.getAmount()) > -1) {
                    //Installments have due date that still more than 3 calendar months cannot be paid.
                    // So if we were in January, you could pay only for January, February and March installments.
                    if (LocalDate.now().withDayOfMonth(2).plusMonths(2).isAfter(loanInstallment.getDueDate())) {
                        //Bonus 2: For reward and penalty add this logic to paying loan flow:
                        BigDecimal calculatedAmount = calculatePaymentAmountByDateDifference(loanInstallment);

                        loanInstallment.setPaidAmount(calculatedAmount);
                        loanInstallment.setPaymentDate(LocalDate.now());
                        loanInstallment.setPaid(true);
                        paidLoanInstallmentList.add(loanInstallment);

                        remainingAmount = remainingAmount.subtract(calculatedAmount);
                        totalAmountSpent = totalAmountSpent.add(calculatedAmount);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            //Necessary updates should be done in customer, loan and installment tables.
            loanInstallmentRepository.saveAll(paidLoanInstallmentList);
            if (unpaidLoanInstallmentList.size() == paidLoanInstallmentList.size()) {
                Loan closedLoan = closeLoan(loanId);
                customerService.decreaseUsedCreditLimit(closedLoan.getCustomer().getId(), closedLoan.getLoanAmount());
            }
        }

        //A result should be returned to inform how many installments paid, total amount spent and if loan is paid completely.
        PaymentInformation paymentInformation;
        paymentInformation = new PaymentInformation(paidLoanInstallmentList.size(),
                                                    totalAmountSpent,
                                                    unpaidLoanInstallmentList != null && unpaidLoanInstallmentList.size() == paidLoanInstallmentList.size());
        return paymentInformation;
    }

    private BigDecimal calculatePaymentAmountByDateDifference(LoanInstallment loanInstallment) {
        long daysBetween = loanInstallment.getDueDate().until(LocalDate.now(), ChronoUnit.DAYS);
        if (daysBetween >= 0) {
            //If an installment is paid before due date,
            // make a discount equal to installmentAmount * 0.001 * (number of days before due date)
            // So in this case paidAmount of installment will be lower than amount.
            BigDecimal discount = loanInstallment.getAmount().multiply(BigDecimal.valueOf(0.001)).multiply(BigDecimal.valueOf(daysBetween));
            return loanInstallment.getAmount().subtract(discount);
        } else {
            //If an installment is paid after due date,
            // add a penalty equal to installmentAmount * 0.001 * (number of days after due date)
            // So in this case paidAmount of installment will be higher than amount.
            BigDecimal penalty = loanInstallment.getAmount().multiply(BigDecimal.valueOf(0.001)).multiply(BigDecimal.valueOf(daysBetween));
            return loanInstallment.getAmount().add(penalty);
        }
    }

    public void createInstallmentsForLoan(Loan loan) {
        // All installments should have same amount. Total amount for loan should be amount * (1 + interest rate)
        BigDecimal totalAmount = loan.getLoanAmount().multiply(loan.getInterestRate().add(BigDecimal.ONE));
        BigDecimal installmentAmount = totalAmount.divide(BigDecimal.valueOf(Integer.parseInt(loan.getNumberOfInstallment())), 13, RoundingMode.HALF_UP);
        LoanInstallment loanInstallment;
        LocalDate dueDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        for (int i = 0; i < Integer.parseInt(loan.getNumberOfInstallment()); i++) {
            loanInstallment = new LoanInstallment();
            loanInstallment.setLoan(loan);
            loanInstallment.setAmount(installmentAmount);
            //Due Date of Installments should be first day of months.
            // So the first installment’s due date should be the first day of next month.
            loanInstallment.setDueDate(dueDate);
            loanInstallment.setPaid(false);
            loanInstallmentRepository.save(loanInstallment);

            dueDate = dueDate.plusMonths(1);
        }
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
}
