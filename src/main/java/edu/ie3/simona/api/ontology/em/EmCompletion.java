/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.ontology.em;

import java.util.Optional;

/**
 * Response send from SIMONA after the em service is finished.
 *
 * @param maybeNextTick option for the next tick in SIMONA
 */
public record EmCompletion(Optional<Long> maybeNextTick) implements EmDataResponseMessageToExt {}
