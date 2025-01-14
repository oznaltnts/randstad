package tr.ozanbey.randstad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.ozanbey.randstad.domain.Customer;
import tr.ozanbey.randstad.domain.Loan;
import tr.ozanbey.randstad.repository.CustomerRepository;

import java.math.BigDecimal;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    //check if customer has enough limit to get this new loan
    public boolean checkEnoughLimit(Loan loan) {
        if (loan.getCustomer().getCreditLimit() == null || loan.getCustomer().getUsedCreditLimit() == null) {
            loan.setCustomer(customerRepository.findById(loan.getCustomer().getId()).get());
        }
        BigDecimal remainingLimit = loan.getCustomer().getCreditLimit().subtract(loan.getCustomer().getUsedCreditLimit());
        return remainingLimit.compareTo(loan.getLoanAmount()) >= 0;
    }

    public void increaseUsedCreditLimit(Long customerId, BigDecimal loanAmount) {
        Customer customer = customerRepository.findById(customerId).get();
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(loanAmount));
        customerRepository.save(customer);
    }

    public void decreaseUsedCreditLimit(Long customerId, BigDecimal loanAmount) {
        Customer customer = customerRepository.findById(customerId).get();
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(loanAmount));
        customerRepository.save(customer);
    }

}
