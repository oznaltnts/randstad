package tr.ozanbey.randstad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.ozanbey.randstad.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
