package tr.ozanbey.randstad.model;

import java.math.BigDecimal;

public class PaymentInformation {
    //A result should be returned to inform how many installments paid,
    // total amount spent and if loan is paid completely.

    private int numberOfInstallmentsPaid;
    private BigDecimal totalAmountSpent;
    private boolean isPaidCompletely;


    public PaymentInformation() {
    }

    public PaymentInformation(int numberOfInstallmentsPaid, BigDecimal totalAmountSpent, boolean isPaidCompletely) {
        this.numberOfInstallmentsPaid = numberOfInstallmentsPaid;
        this.totalAmountSpent = totalAmountSpent;
        this.isPaidCompletely = isPaidCompletely;
    }

    public int getNumberOfInstallmentsPaid() {
        return numberOfInstallmentsPaid;
    }

    public void setNumberOfInstallmentsPaid(int numberOfInstallmentsPaid) {
        this.numberOfInstallmentsPaid = numberOfInstallmentsPaid;
    }

    public BigDecimal getTotalAmountSpent() {
        return totalAmountSpent;
    }

    public void setTotalAmountSpent(BigDecimal totalAmountSpent) {
        this.totalAmountSpent = totalAmountSpent;
    }

    public boolean isPaidCompletely() {
        return isPaidCompletely;
    }

    public void setPaidCompletely(boolean paidCompletely) {
        isPaidCompletely = paidCompletely;
    }
}
