/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.system.SystemParticipantResult;
import edu.ie3.simona.api.data.ExtDataSimulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An external simulation that needs results from SIMONA should implement this interface and handle
 * the ExtResultsData that is handed over.
 */
public class ExtResultDataSimulation implements ExtDataSimulation {

  private final Map<UUID, String> gridResultAssetMapping;
  private final Map<UUID, String> participantResultAssetMapping;
  private final List<UUID> particpantResultDataAssets;
  private final List<UUID> gridResultDataAssets;

  private ExtResultData extResultData;

  public ExtResultDataSimulation(
          Map<UUID, String> participantResultAssetMapping,
          Map<UUID, String> gridResultAssetMapping
  ) {
    this.participantResultAssetMapping = participantResultAssetMapping;
    this.gridResultAssetMapping = gridResultAssetMapping;
    this.particpantResultDataAssets = participantResultAssetMapping.keySet().stream().toList();
    this.gridResultDataAssets = gridResultAssetMapping.keySet().stream().toList();
  }

  /** Hand over an ExtPrimaryData which enables communication regarding primary data. */
  public void setExtResultData(ExtResultData extResultData) {
    this.extResultData = extResultData;
  }

  public List<UUID> getParticipantResultDataAssets() {
    return particpantResultDataAssets;
  }
  public List<UUID> getGridResultDataAssets() {
    return gridResultDataAssets;
  }

  public Map<String, ResultEntity> requestResults(Long tick) throws InterruptedException {
    return createResultMap(extResultData.requestResults(tick));
  }

  private Map<String, ResultEntity> createResultMap(Map<UUID, ResultEntity> results) {
    Map<String, ResultEntity> resultMap = new HashMap<>();
    results.forEach(
            (uuid, res) -> {
              if (res instanceof NodeResult) {
                resultMap.put(gridResultAssetMapping.get(res.getInputModel()), res);
              } else if (res instanceof SystemParticipantResult) {
                resultMap.put(participantResultAssetMapping.get(res.getInputModel()), res);
              } else {
                throw new RuntimeException();
              }
            }
    );
    return resultMap;
  }

  public ExtResultData getExtResultData() {
    return extResultData;
  }
}
