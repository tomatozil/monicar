package org.collector.application;

import static org.collector.common.constant.CycleInfoSize.*;

import java.util.List;

import org.collector.common.exception.CustomException;
import org.collector.common.response.ResponseCode;
import org.collector.domain.CycleInfo;
import org.collector.domain.VehicleInformation;
import org.collector.infrastructure.repository.CycleInfoRepository;
import org.collector.infrastructure.repository.VehicleInformationRepository;
import org.collector.presentation.dto.CListRequest;
import org.collector.presentation.dto.CycleInfoRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CycleInfoService {
	private final CycleInfoRepository cycleInfoRepository;
	private final VehicleInformationRepository vehicleInformationRepository;

	@Transactional
	public Long cycleInfoSave(final CycleInfoRequest request) {
		VehicleInformation vehicleInfo = vehicleInformationRepository.findByMdn(request.mdn())
			.orElseThrow(() -> new CustomException(ResponseCode.ENTITY_NOT_FOUND));

		saveVehicleLocation(vehicleInfo, request);

		List<CycleInfo> cycleInfos = request.cList().stream()
			.map(cListRequest ->
				CycleInfo.from(cListRequest, vehicleInfo))
			.toList();

		cycleInfoRepository.saveAll(cycleInfos);
		return vehicleInfo.getMdn();
	}

	private void saveVehicleLocation(final VehicleInformation vehicleInformation, final CycleInfoRequest request) {
		CListRequest cListRequest = request.cList().get(request.cList().size()-1);
		vehicleInformation.saveLocation(cListRequest.lat(), cListRequest.lng());
		vehicleInformationRepository.save(vehicleInformation);
	}
}
