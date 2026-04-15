/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.model.em;

/** Interface that defines that a class can be part of an em communication message. */
public sealed interface EmMessageContent permits FlexOptionRequest, FlexOptions, SetPoint {}
