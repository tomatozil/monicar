package org.eventhub.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CycleInfo {
	private long id;
	private long vehicleId;
	private LocalDateTime intervalAt;
	private GpsStatus gcd;
	private int lat;
	private int lng;
	private int ang;
	private int spd;
	private LocalDateTime createdAt;
}
