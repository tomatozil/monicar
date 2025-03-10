package org.eventhub.application;

import java.util.Optional;

import org.eventhub.common.exception.BusinessException;
import org.eventhub.common.response.ErrorCode;
import org.eventhub.domain.DrivingHistoryCreate;
import org.eventhub.domain.UpdateTotalDistance;
import org.eventhub.domain.VehicleEvent;
import org.eventhub.domain.VehicleEventType;
import org.eventhub.domain.VehicleInformation;
import org.eventhub.domain.VehicleOffEventCreate;
import org.eventhub.domain.VehicleOnEventCreate;
import org.eventhub.domain.VehicleStatus;
import org.eventhub.presentation.request.KeyOffRequest;
import org.eventhub.presentation.request.KeyOnRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class OnOffService {
	private final VehicleService vehicleService;
	private final VehicleEventService vehicleEventService;
	private final DrivingHistoryService drivingHistoryService;

	@Transactional
	public void keyOn(final KeyOnRequest request) {
		VehicleInformation vehicleInformation = vehicleService.getVehicleInformation(request.mdn());

		Optional<VehicleEvent> vehicleEvent = vehicleEventService.getRecentVehicleEvent(vehicleInformation.getId());
		boolean isAlreadyOn = vehicleEvent.map(VehicleEvent::isTypeOn).orElse(false);

		log.info("is already ON? : {}! {}", isAlreadyOn, isAlreadyOn ? "" : "Engine ON");
		if (isAlreadyOn) {
			throw new BusinessException(ErrorCode.WRONG_APPROACH);
		}
		VehicleOnEventCreate vehicleOnEventCreate = request.toDomain(vehicleInformation.getId(), vehicleInformation.getSum());
		vehicleEventService.saveVehicleEvent(vehicleOnEventCreate);

		vehicleService.updateVehicleStatus(vehicleInformation.getId(), VehicleStatus.IN_OPERATION);
	}

	@Transactional
	public void keyOff(final KeyOffRequest request) {
		VehicleInformation vehicleInformation = vehicleService.getVehicleInformation(request.mdn());

		Optional<VehicleEvent> vehicleEvent = vehicleEventService.getRecentVehicleEvent(vehicleInformation.getId());
		boolean isAlreadyOff = vehicleEvent.map(VehicleEvent::isTypeOff).orElse(false);

		log.info("is already OFF? : {}! {}", isAlreadyOff, isAlreadyOff ? "" : "Engine OFF");
		if (isAlreadyOff) {
			throw new BusinessException(ErrorCode.WRONG_APPROACH);
		}

		vehicleService.updateVehicleStatus(vehicleInformation.getId(), VehicleStatus.NOT_DRIVEN);

		Long updatedTotalDistance = vehicleService.updateTotalDistance(UpdateTotalDistance.of(
			vehicleInformation.getId(), request.sum()
		));

		drivingHistoryService.saveDrivingHistory(DrivingHistoryCreate.of(
				vehicleInformation.getId(),
				vehicleInformation.getCompanyId(),
				updatedTotalDistance,
				request.sum(),
				vehicleEvent.get().getEventAt(),
				request.offTime()
			)
		);

		vehicleEventService.saveVehicleEvent(VehicleOffEventCreate.of(
			vehicleInformation.getId(),
			VehicleEventType.OFF,
			request.offTime()
		));
	}
}
