package org.eventhub.application.port;

import java.util.List;

import org.eventhub.domain.CycleInfo;

public interface CycleInfoRepository {
	List<CycleInfo> saveAll(List<CycleInfo> cycleInfos);
	void saveAllBatch(List<CycleInfo> cycleInfos);
}
