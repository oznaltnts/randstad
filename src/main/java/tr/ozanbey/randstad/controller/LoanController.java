package tr.ozanbey.randstad.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.ozanbey.randstad.domain.Loan;
import tr.ozanbey.randstad.domain.LoanInstallment;
import tr.ozanbey.randstad.model.PaymentInformation;
import tr.ozanbey.randstad.service.CustomerService;
import tr.ozanbey.randstad.service.LoanInstallmentService;
import tr.ozanbey.randstad.service.LoanService;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class LoanController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanInstallmentService loanInstallmentService;

    //Create a new loan for a given customer, amount, interest rate and number of installments
    @PostMapping(path = "/create-loan", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Loan> createLoan(@Valid @RequestBody Loan loan) throws Exception {
        if (customerService.checkEnoughLimit(loan)) {
            Loan savedLoan = loanService.createLoan(loan);
            return new ResponseEntity<Loan>(savedLoan, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().body(loan);
        }
    }

    //List loans for a given customer
    @GetMapping("/list-loans/{id}")
    public ResponseEntity<List<Loan>> getAllLoansByCustomerId(@PathVariable(name = "id") Long customerId) {
        List<Loan> customerLoanList = loanService.getAllLoans(customerId);
        return ResponseEntity.ok(customerLoanList);
    }

    //List installments for a given loan
    @GetMapping("/list-installments/{id}")
    public ResponseEntity<List<LoanInstallment>> getAllInstallmentsByLoanId(@PathVariable(name = "id") Long loanId) {
        //TODO  -	If you want you can add more filters like number of installments, is paid etc
        List<LoanInstallment> loanLoanInstallmentList = loanInstallmentService.getAllInstallments(loanId);
        return ResponseEntity.ok(loanLoanInstallmentList);
    }

    //Pay installment for a given loan and amount
    @PutMapping("/pay-loan/{id}/{amount}")
    public ResponseEntity<PaymentInformation> payLoanByIdAndAmount(@PathVariable(name = "id") Long loanId,
                                                                   @PathVariable(name = "amount") BigDecimal amount) {
        PaymentInformation paymentInformation = loanInstallmentService.payInstallmentByLoanIdAndAmount(loanId, amount);
        return ResponseEntity.ok(paymentInformation);
    }

}
