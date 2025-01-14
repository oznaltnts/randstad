package tr.ozanbey.randstad.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tr.ozanbey.randstad.domain.Loan;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LoanCriteria {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<Loan> findByFilter(Long customerId, String numberOfInstallments, String isPaid) {
        CriteriaQuery<Loan> loanCriteria = createFindByCriteriaQuery(customerId, numberOfInstallments, isPaid);
        return entityManager.createQuery(loanCriteria).getResultList();
    }

    private CriteriaQuery<Loan> createFindByCriteriaQuery(Long customerId, String numberOfInstallments, String isPaid) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Loan> criteriaQuery = criteriaBuilder.createQuery(Loan.class);
        Root<Loan> loanRoot = criteriaQuery.from(Loan.class);
        List<Predicate> predicates = createPredicates(loanRoot, customerId, numberOfInstallments, isPaid);
        criteriaQuery.distinct(true);
        return criteriaQuery.where(predicates.toArray(new Predicate[0]));
    }

    private <T> List<Predicate> createPredicates(Root<Loan> loanRoot, Long customerId, String numberOfInstallments, String isPaid) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(loanRoot.get("customer").get("id"), customerId));
        if (numberOfInstallments != null && !numberOfInstallments.isEmpty()) {
            predicates.add(criteriaBuilder.equal(loanRoot.get("numberOfInstallment"), numberOfInstallments));
        }
        if (isPaid != null && !isPaid.isEmpty()) {
            predicates.add(criteriaBuilder.equal(loanRoot.get("isPaid"), isPaid));
        }
        return predicates;
    }
}
