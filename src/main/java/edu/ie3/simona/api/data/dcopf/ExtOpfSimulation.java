/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.simona.api.data.dcopf;

import edu.ie3.simona.api.data.ExtDataSimulation;

public interface ExtOpfSimulation extends ExtDataSimulation {

  void setExtOpfData(ExtOpfData opfData);
}
