/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import edu.ie3.simona.api.data.model.em.EmData;
import edu.ie3.simona.api.data.model.em.FlexOptionRequest;
import edu.ie3.simona.api.data.model.em.FlexOptions;
import edu.ie3.simona.api.data.model.em.SetPoint;
import java.util.*;
import org.slf4j.Logger;

/** Message that provides em data from an external simulation. */
public record ProvideEmData(
    long tick,
    Map<UUID, FlexOptionRequest> flexRequests,
    Map<UUID, List<FlexOptions>> flexOptions,
    Map<UUID, SetPoint> setPoints)
    implements EmDataMessageFromExt {

  public static ProvideEmData create(long tick, Map<UUID, ? extends EmData> emData, Logger log) {
    Map<UUID, FlexOptionRequest> flexRequests = new HashMap<>();
    Map<UUID, List<FlexOptions>> flexOptions = new HashMap<>();
    Map<UUID, SetPoint> setPoints = new HashMap<>();

    emData.forEach(
        (uuid, emDataItem) -> {
          switch (emDataItem) {
            case FlexOptionRequest r -> flexRequests.put(uuid, r);
            case FlexOptions r ->
                flexOptions.computeIfAbsent(uuid, receiver -> new ArrayList<>()).add(r);
            case SetPoint r -> setPoints.put(uuid, r);
            default -> log.warn("Received unsupported em data: {}", emDataItem);
          }
        });

    return new ProvideEmData(tick, flexRequests, flexOptions, setPoints);
  }
}
