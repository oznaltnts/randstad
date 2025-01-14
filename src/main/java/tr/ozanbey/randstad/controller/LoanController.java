package tr.ozanbey.randstad.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.ozanbey.randstad.domain.Loan;
import tr.ozanbey.randstad.domain.LoanInstallment;
import tr.ozanbey.randstad.domain.User;
import tr.ozanbey.randstad.model.PaymentInformation;
import tr.ozanbey.randstad.service.CustomerService;
import tr.ozanbey.randstad.service.LoanInstallmentService;
import tr.ozanbey.randstad.service.LoanService;
import tr.ozanbey.randstad.service.UserService;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class LoanController {
    //All endpoints should be authorized with an admin user and password

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanInstallmentService loanInstallmentService;

    @Autowired
    private UserService userService;

    //Create a new loan for a given customer, amount, interest rate and number of installments
    @PostMapping(path = "/create-loan", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Loan> createLoan(@RequestHeader("adminuser") String adminuser,
                                           @RequestHeader("password") String password,
                                           @Valid @RequestBody Loan loan) throws Exception {
        User admin = userService.getUserById(adminuser, password);
        if (admin != null) {
            if (customerService.checkEnoughLimit(loan)) {
                Loan savedLoan = loanService.createLoan(loan);
                return new ResponseEntity<Loan>(savedLoan, HttpStatus.CREATED);
            }
        }
        return null;
    }

    //List loans for a given customer
    @GetMapping("/list-loans/{id}")
    public ResponseEntity<List<Loan>> getAllLoansByCustomerId(@RequestHeader("adminuser") String adminuser,
                                                              @RequestHeader("password") String password,
                                                              @PathVariable(name = "id") Long customerId,
                                                              @RequestParam(required = false) String numberOfInstallments,
                                                              @RequestParam(required = false) String isPaid) {
        User admin = userService.getUserById(adminuser, password);
        if (admin != null) {
            //If you want you can add more filters like number of installments, is paid etc
            List<Loan> customerLoanList = loanService.getAllLoans(customerId, numberOfInstallments, isPaid);
            return ResponseEntity.ok(customerLoanList);
        }
        return null;
    }

    //List installments for a given loan
    @GetMapping("/list-installments/{id}")
    public ResponseEntity<List<LoanInstallment>> getAllInstallmentsByLoanId(@RequestHeader("adminuser") String adminuser,
                                                                            @RequestHeader("password") String password,
                                                                            @PathVariable(name = "id") Long loanId) {
        User admin = userService.getUserById(adminuser, password);
        if (admin != null) {
            List<LoanInstallment> loanLoanInstallmentList = loanInstallmentService.getAllInstallments(loanId);
            return ResponseEntity.ok(loanLoanInstallmentList);
        }
        return null;
    }

    //Pay installment for a given loan and amount
    @PutMapping("/pay-loan/{id}/{amount}")
    public ResponseEntity<PaymentInformation> payLoanByIdAndAmount(@RequestHeader("adminuser") String adminuser,
                                                                   @RequestHeader("password") String password,
                                                                   @PathVariable(name = "id") Long loanId,
                                                                   @PathVariable(name = "amount") BigDecimal amount) {
        User admin = userService.getUserById(adminuser, password);
        if (admin != null) {
            PaymentInformation paymentInformation = loanInstallmentService.payInstallmentByLoanIdAndAmount(loanId, amount);
            return ResponseEntity.ok(paymentInformation);
        }
        return null;
    }

}
