/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.ev.ontology;

import edu.ie3.simona.api.data.ev.model.EvModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllDepartedEvsResponse implements EvDataResponseMessageToExt {
  private final List<EvModel> departedEvs;

  /** No departed evs during current tick */
  public AllDepartedEvsResponse() {
    this.departedEvs = new ArrayList<>(0);
  }

  public AllDepartedEvsResponse(List<EvModel> departedEvs) {
    this.departedEvs = departedEvs;
  }

  public List<EvModel> getDepartedEvs() {
    return departedEvs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AllDepartedEvsResponse that = (AllDepartedEvsResponse) o;

    return departedEvs.equals(that.departedEvs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(departedEvs);
  }
}
