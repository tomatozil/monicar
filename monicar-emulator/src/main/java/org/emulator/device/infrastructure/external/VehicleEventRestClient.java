package org.emulator.device.infrastructure.external;

import java.util.List;

import org.emulator.device.application.port.VehicleEventSender;
import org.emulator.device.common.response.BaseResponse;
import org.emulator.device.domain.CycleInfo;
import org.emulator.device.domain.OffInfo;
import org.emulator.device.domain.OnInfo;
import org.emulator.device.infrastructure.external.command.CycleInfoListCommand;
import org.emulator.device.infrastructure.external.command.OffCommand;
import org.emulator.device.infrastructure.external.command.OnCommand;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class VehicleEventRestClient implements VehicleEventSender {
	private final RestClientService restClientService;

	@Override
	public BaseResponse sendOnEvent(OnInfo onInfo) {
		OnCommand onCommand = OnCommand.from(onInfo);

		return restClientService.post("key-on", onCommand);
	}

	@Override
	public BaseResponse sendOffEvent(OffInfo offInfo) {
		OffCommand offCommand = OffCommand.from(offInfo);

		return restClientService.post("key-off", offCommand);
	}

	@Override
	public BaseResponse sendCycleInfoEvent(List<CycleInfo> cycleInfo) {
		CycleInfoListCommand cycleInfoListCommand = CycleInfoListCommand.from(cycleInfo);

		// return restClientService.post("cycle-info", cycleInfoListCommand);
		return restClientService.post("cycle-info/redirect", cycleInfoListCommand);
	}
}
