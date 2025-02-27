package org.eventhub.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.eventhub.application.port.CycleInfoRepository;
import org.eventhub.domain.CycleInfo;
import org.eventhub.infrastructure.repository.jpa.CycleInfoJpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class CycleInfoRepositoyAdapter implements CycleInfoRepository {
	private final CycleInfoJpaRepository jpaRepository;
	private final JdbcTemplate jdbcTemplate;

	@Override
	public void saveAll(List<CycleInfo> cycleInfos) {
		log.info("[3]event hub first interval at: {}", cycleInfos.get(0).getIntervalAt());
	// 	return jpaRepository.saveAll(cycleInfos.stream().map(CycleInfoEntity::from).toList())
	// 		.stream()
	// 		.map(CycleInfoEntity::toDomain)
	// 		.toList();
		String sql = """
            INSERT INTO cycle_info (vehicle_id, interval_at, status, lat, lng, ang, spd, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

		List<Object[]> batchArgs = cycleInfos.stream()
			.map(cycleInfo -> new Object[]{
				cycleInfo.getVehicleId(),
				cycleInfo.getIntervalAt().atZone(ZoneId.of("UTC"))
					.withZoneSameInstant(ZoneId.of("Asia/Seoul"))
					.toLocalDateTime(),
				cycleInfo.getGcd().name(),
				cycleInfo.getLat(),
				cycleInfo.getLng(),
				cycleInfo.getAng(),
				cycleInfo.getSpd(),
				LocalDateTime.now(ZoneId.of("Asia/Seoul"))
			})
			.toList();

		// 🔹 Batch Insert 실행
		jdbcTemplate.batchUpdate(sql, batchArgs);
	}
}
