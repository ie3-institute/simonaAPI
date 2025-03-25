/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.em;

import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.simona.api.data.BiDirectional;
import edu.ie3.simona.api.data.em.model.FlexOptionRequestValue;
import edu.ie3.simona.api.data.em.model.FlexOptions;
import edu.ie3.simona.api.data.em.ontology.*;
import edu.ie3.simona.api.simulation.mapping.ExtEntityMapping;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;

/** Enables data connection of em data between SIMONA and SimonaAPI */
public class ExtEmDataConnection
    extends BiDirectional<EmDataMessageFromExt, EmDataResponseMessageToExt> {

  /** Assets that provide data to SIMONA */
  private final Map<String, UUID> extEmMapping;

  /** Assets that provide data to ext */
  private final Map<UUID, String> mosaikMapping;

  public ExtEmDataConnection(Map<String, UUID> extEmMapping) {
    super(new EmCompletion());

    this.extEmMapping = extEmMapping;

    this.mosaikMapping =
        extEmMapping.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
  }

  /** Returns a list of the uuids of the em agents that expect external set points */
  public List<UUID> getControlledEms() {
    return new ArrayList<>(extEmMapping.values());
  }


  public void convertAndSendFlexRequests(
          long tick, Map<String, FlexOptionRequestValue> data, Optional<Long> maybeNextTick, Logger log) {
    // filtering the data and converting the keys
    Map<UUID, List<UUID>> emFlexRequests = data.values().stream().map(value -> {
      UUID requester = extEmMapping.get(value.requester());
      List<UUID> emEntities = ExtEntityMapping.toSimona(value.emEntities(), extEmMapping);

      return Map.entry(requester, emEntities);
    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    if (emFlexRequests.isEmpty()) {
      log.warn("No em flex requests found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em flex requests.");
      sendExtMsg(new ProvideFlexRequestData(tick, emFlexRequests, maybeNextTick));
    }
  }


  /**
   * Converts the em flex options and sends them to SIMONA.
   *
   * @param tick current tick
   * @param data to be converted and send
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public void convertAndSendFlexOptions(
      long tick, Map<String, List<FlexOptions>> data, Optional<Long> maybeNextTick, Logger log) {
    // filtering the data and converting the keys
    Map<String, Map<UUID, FlexOptions>> inputData = new HashMap<>();
    data.forEach((receiver, options) -> inputData.put(receiver, ExtEntityMapping.mapToSimona(options.stream().collect(Collectors.toMap(FlexOptions::sender, i -> i)), extEmMapping)));


    Map<UUID, Map<UUID, FlexOptions>> emFlexOptions = ExtEntityMapping.mapToSimona(inputData, extEmMapping);

    if (emFlexOptions.isEmpty()) {
      log.warn("No em flex options found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em flex options.");
      sendExtMsg(new ProvideEmFlexOptionData(tick, emFlexOptions, maybeNextTick));
    }
  }

  /**
   * Converts the em set points and sends them to SIMONA.
   *
   * @param tick current tick
   * @param data to be converted and send
   * @param maybeNextTick option for the next tick in the simulation
   * @param log logger
   */
  public void convertAndSendSetPoints(
      long tick, Map<String, PValue> data, Optional<Long> maybeNextTick, Logger log) {
    // filtering the data and converting the keys
    Map<UUID, PValue> emSetPoints = ExtEntityMapping.mapToSimona(data, extEmMapping);

    if (emSetPoints.isEmpty()) {
      log.warn("No em set points found! Sending no em data to SIMONA for tick {}.", tick);
    } else {
      log.debug("Provided SIMONA with em set points.");
      sendExtMsg(new ProvideEmSetPointData(tick, emSetPoints, maybeNextTick));
    }
  }

  /**
   * Converts and requests em flexibility options from SIMONA.
   *
   * @param tick for which set points are requested
   * @param emEntities for which set points are requested
   * @return an {@link FlexOptionsResponse} message
   * @throws InterruptedException - on interruptions
   */
  public Map<String, ResultEntity> convertAndSendRequestFlexResults(
      long tick, Map<String, FlexOptionRequestValue> emEntities, Logger log)
      throws InterruptedException {
    Map<String, List<UUID>> m =
        emEntities.entrySet().stream()
            .map(
                e ->
                    Map.entry(
                        e.getKey(),
                        ExtEntityMapping.toSimona(e.getValue().emEntities(), extEmMapping)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Map<UUID, List<UUID>> map = ExtEntityMapping.mapToSimona(m, extEmMapping);
    log.info("Provided SIMONA with em flex request: {}", map);

    return requestEmFlexResults(tick, map);
  }

  /**
   * Method to request em flexibility options from SIMONA.
   *
   * @param tick for which set points are requested
   * @param emEntities for which set points are requested
   * @return an {@link FlexOptionsResponse} message
   * @throws InterruptedException - on interruptions
   */
  public Map<String, ResultEntity> requestEmFlexResults(long tick, Map<UUID, List<UUID>> emEntities)
      throws InterruptedException {
    sendExtMsg(new RequestEmFlexResults(tick, emEntities));
    return ExtEntityMapping.mapToExt(
        receiveWithType(FlexOptionsResponse.class).flexOptions(), mosaikMapping);
  }

  /**
   * Method to request em set points from SIMONA.
   *
   * @param tick for which set points are requested
   * @param emEntities for which set points are requested
   * @return an {@link EmSetPointDataResponse} message
   * @throws InterruptedException - on interruptions
   */
  public Map<String, ResultEntity> requestEmSetPoints(long tick, List<UUID> emEntities)
      throws InterruptedException {
    sendExtMsg(new RequestEmSetPoints(tick, emEntities));
    return ExtEntityMapping.mapToExt(
        receiveWithType(EmSetPointDataResponse.class).emData(), mosaikMapping);
  }
}
