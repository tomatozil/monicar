package org.eventhub.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import lombok.Builder;

import org.eventhub.domain.CycleInfo;
import org.eventhub.domain.GpsStatus;

@Builder
public record SimpleCycleInfoRequest(
	@JsonFormat(pattern = "yyyyMMddHHmmss") LocalDateTime intervalAt,
	GpsStatus gcd,
	Integer lat,
	Integer lng,
	Integer ang,
	Integer spd
) {
	public CycleInfo toDomain() {
		return CycleInfo.builder()
			.intervalAt(intervalAt)
			.gcd(gcd)
			.lat(lat)
			.lng(lng)
			.ang(ang)
			.spd(spd)
			.build();
	}
}
