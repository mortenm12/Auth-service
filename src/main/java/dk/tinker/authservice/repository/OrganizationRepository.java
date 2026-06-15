package dk.tinker.authservice.repository;

import dk.tinker.authservice.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT o FROM Organization o WHERE o.parent IS NULL")
    List<Organization> findByParentIsNull();

    @Query("SELECT o FROM Organization o WHERE o.parent.id = :parentId")
    List<Organization> findByParentId(@Param("parentId") UUID parentId);
}
