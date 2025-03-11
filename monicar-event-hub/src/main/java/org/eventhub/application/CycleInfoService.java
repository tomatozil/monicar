package org.eventhub.application;

import java.util.List;

import org.eventhub.application.port.CycleInfoRepository;
import org.eventhub.application.port.VehicleRepository;
import org.eventhub.common.exception.BusinessException;
import org.eventhub.common.response.ErrorCode;
import org.eventhub.domain.CycleInfo;
import org.eventhub.domain.CycleInfoList;
import org.eventhub.domain.VehicleInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CycleInfoService {
	private final VehicleRepository vehicleRepository;
	private final CycleInfoRepository cycleInfoRepository;

	@Transactional
	public void saveCycleInfos(CycleInfoList cycleInfoList) {
		VehicleInformation vehicleInfo = vehicleRepository.findByMdn(cycleInfoList.getMdn())
			.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

		saveVehicleLocation(vehicleInfo.getId(), cycleInfoList);

		List<CycleInfo> cycleInfos = cycleInfoList.getCList().stream()
			.map(c -> CycleInfo.builder()
						.vehicleId(vehicleInfo.getId())
						.intervalAt(c.getIntervalAt())
						.gcd(c.getGcd())
						.lat(c.getLat())
						.lng(c.getLng())
						.ang(c.getAng())
						.spd(c.getSpd())
						.build()
			).toList();
		cycleInfoRepository.saveAllBatch(cycleInfos);
		// cycleInfoRepository.saveAll(cycleInfos);
	}

	private VehicleInformation saveVehicleLocation(Long vehicleId, CycleInfoList cycleInfoList) {
		CycleInfo cycleInfo = cycleInfoList
			.getCList()
			.get(cycleInfoList.getCCnt()-1);

		return vehicleRepository.updateVehicleLocation(
			vehicleId,
			cycleInfo.getLat(),
			cycleInfo.getLng()
		);
	}
}
