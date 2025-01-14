package tr.ozanbey.randstad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.ozanbey.randstad.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndPassword(String username, String password);
}
