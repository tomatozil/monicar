package org.eventhub.infrastructure.repository.jpa;

import org.eventhub.infrastructure.repository.jpa.entity.CycleInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CycleInfoJpaRepository extends JpaRepository<CycleInfoEntity, Long> {
}
