package tr.ozanbey.randstad.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan implements Serializable {
    //Loan: id, customerId, loanAmount, numberOfInstallment, createDate, isPaid

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @NotNull(message = "Loan amount is required")
    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount;

    @NotNull(message = "Number of installments is required")
    @Column(name = "number_of_installment", nullable = false)
    @Pattern(regexp = "^(6|9|12|24)$", message = "Number of installments can only be 6, 9, 12, 24")
    private String numberOfInstallment;

    @Column(name = "create_date", nullable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name = "is_paid", nullable = false, columnDefinition = "TINYINT")
    private boolean isPaid;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.1", message = "Interest rate can be between 0.1 – 0.5")
    @DecimalMax(value = "0.5", message = "Interest rate can be between 0.1 – 0.5")
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    public Loan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getNumberOfInstallment() {
        return numberOfInstallment;
    }

    public void setNumberOfInstallment(String numberOfInstallment) {
        this.numberOfInstallment = numberOfInstallment;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
