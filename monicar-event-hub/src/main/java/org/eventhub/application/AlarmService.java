package org.eventhub.application;

import java.util.Optional;

import org.eventhub.application.port.AlarmRepository;
import org.eventhub.application.port.AlarmSender;
import org.eventhub.domain.Alarm;
import org.eventhub.domain.AlarmStatus;
import org.eventhub.domain.VehicleInformation;
import org.eventhub.infrastructure.external.command.AlarmSend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlarmService {
	private final VehicleService vehicleService;
	private final AlarmRepository alarmRepository;
	private final AlarmSender alarmSender;
	@Value("${alarm-interval-distance}")
	private Integer alarmIntervalDistance;

	public Optional<Alarm> findByVehicleId(Long vehicleId) {
		return alarmRepository.findByVehicleId(vehicleId);
	}

	@Async
	public void sendAlarmIfNecessary(Long mdn) {
		VehicleInformation vehicleInfo = vehicleService.getVehicleInformation(mdn);
		Long vehicleId = vehicleInfo.getId();
		Long totalDistance = vehicleInfo.getSum();
		try {
			Optional<Alarm> alarm = alarmRepository.findRecentOneByVehicleId(vehicleId);
			int targetDistance = alarm.map(Alarm::getDrivingDistance).orElse(0);

			if (checkBiggerThanIntervalDistance(totalDistance, targetDistance)) {
				if (alarm.isEmpty() || alarm.get().getStatus().equals(AlarmStatus.COMPLETED)) {
					Long alarmId = alarmRepository.save(vehicleId);
					sendAlarm(alarmId);
				}
			}
		} catch (Exception e) {
			log.error("알림 로직 예외 발생", e);
		}
	}

	private void sendAlarm(Long alarmId) {
		alarmSender.sendAlarm(AlarmSend.builder()
			.alarmId(alarmId)
			.build());
	}

	private boolean checkBiggerThanIntervalDistance(long totalDistance, int drivingDistanceLastCheck) {
		return totalDistance - drivingDistanceLastCheck >= alarmIntervalDistance;
	}
}
