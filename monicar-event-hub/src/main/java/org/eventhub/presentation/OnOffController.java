package org.eventhub.presentation;

import org.eventhub.application.AlarmService;
import org.eventhub.application.OnOffService;
import org.eventhub.common.response.BaseResponse;
import org.eventhub.presentation.request.KeyOffRequest;
import org.eventhub.presentation.request.KeyOnRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/event-hub")
@Slf4j
public class OnOffController {
	private final OnOffService onOffService;
	private final AlarmService alarmService;

	@PostMapping("/key-on")
	public BaseResponse keyOn(
		@Valid @RequestBody final KeyOnRequest request
	) {
		log.info("emulator ON request in event-hub ! ");
		onOffService.keyOn(request);

		return BaseResponse.success();
	}

	@PostMapping("/key-off")
	public BaseResponse keyOff(
		@Valid @RequestBody final KeyOffRequest request
	) {
		log.info("emulator OFF request in event-hub ! ");

		onOffService.keyOff(request);
		alarmService.sendAlarmIfNecessary(request.mdn());

		return BaseResponse.success();
	}
}
