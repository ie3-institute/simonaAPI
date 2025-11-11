/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

import java.util.UUID;

/**
 * Energy management flex option request that will be sent to SIMONA.
 *
 * @param receiver The receiver of the request.
 * @param disaggregated True, if disaggregated flex option should be returned.
 */
public record FlexOptionRequest(UUID receiver, boolean disaggregated, boolean releaseControl) implements EmData {
    public FlexOptionRequest(UUID receiver, boolean disaggregated) {
        this(receiver, disaggregated, false);
    }
    
    public static FlexOptionRequest releaseControl(UUID receiver) {
        return new FlexOptionRequest(receiver, false, true);
    }
}
