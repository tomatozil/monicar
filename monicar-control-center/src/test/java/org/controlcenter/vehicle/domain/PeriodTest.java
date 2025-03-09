package org.controlcenter.vehicle.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PeriodTest {
	@Test
	void asd() {
		// arrange

		// act
		LocalDate minus = Period.MONTH.minus(LocalDate.now());

		// assert
		// assertThat(minus).isEqualTo(LocalDate.of(2025, 1, 15));
	}

}