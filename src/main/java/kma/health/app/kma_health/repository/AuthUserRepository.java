package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface AuthUserRepository<T extends AuthUser> extends JpaRepository<T, UUID> {
    Optional<T> findByEmail(String email);
    Optional<T> findByPhoneNumber(String phone);
    Optional<T> findByPassportNumber(String passport);
}
