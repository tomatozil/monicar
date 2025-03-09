package org.eventhub.application.port;

import java.util.List;

import org.eventhub.domain.CycleInfo;

public interface CycleInfoRepository {
	void saveAll(List<CycleInfo> cycleInfos);
}
