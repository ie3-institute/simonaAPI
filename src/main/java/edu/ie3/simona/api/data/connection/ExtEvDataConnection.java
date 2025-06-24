/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.connection;

import edu.ie3.simona.api.data.ev.model.EvModel;
import edu.ie3.simona.api.data.ev.ontology.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ExtEvDataConnection
    extends BiDirectional<EvDataMessageFromExt, EvDataResponseMessageToExt> {

  public ExtEvDataConnection() {
    super();
  }

  /**
   * Requests currently available evcs charging stations lots from SIMONA. This method blocks until
   * having received a response from SIMONA.
   *
   * @return a mapping from evcs uuid to the amount of available charging station lots
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public Map<UUID, Integer> requestAvailablePublicEvcs() throws InterruptedException {
    sendExtMsg(new RequestEvcsFreeLots());

    return receiveWithType(ProvideEvcsFreeLots.class).evcs();
  }

  /**
   * Requests prices at all EVCS station at current tick. This method blocks until having received a
   * response from SIMONA.
   *
   * @return mapping from evcs uuid to current price
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public Map<UUID, Double> requestCurrentPrices() throws InterruptedException {
    sendExtMsg(new RequestCurrentPrices());

    return receiveWithType(ProvideCurrentPrices.class).prices();
  }

  /**
   * Request the charged EVs that are departing from their charging stations at the current tick.
   * SIMONA returns the charged departing vehicles with updated battery SOC. This method blocks
   * until having received a response from SIMONA.
   *
   * @param departures the departing EV UUIDs per charging station UUID
   * @return all charged departing vehicles
   * @throws InterruptedException if the thread running this has been interrupted during the
   *     blocking operation
   */
  public List<EvModel> requestDepartingEvs(Map<UUID, List<UUID>> departures)
      throws InterruptedException {
    sendExtMsg(new RequestDepartingEvs(departures));

    return receiveWithType(ProvideDepartingEvs.class).departedEvs();
  }

  /**
   * Provide all EVs that are arriving at some charging station to SIMONA. Method returns right away
   * without expecting an answer from SIMONA.
   *
   * @param arrivals the arriving EV models per charging station UUID
   * @param maybeNextTick the next tick at which new arrivals are expected, or empty if simulation
   *     is about to end
   */
  public void provideArrivingEvs(Map<UUID, List<EvModel>> arrivals, Optional<Long> maybeNextTick) {
    sendExtMsg(new ProvideArrivingEvs(arrivals, maybeNextTick));
  }
}
