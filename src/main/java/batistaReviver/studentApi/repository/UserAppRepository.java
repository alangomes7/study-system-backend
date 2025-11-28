package batistaReviver.studentApi.repository;

import batistaReviver.studentApi.model.UserApp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAppRepository extends JpaRepository<UserApp, Long> {
  Optional<UserApp> findByEmail(String email);
}
