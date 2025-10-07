package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Declaration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeclarationRepository extends JpaRepository<Declaration, UUID> {
}
