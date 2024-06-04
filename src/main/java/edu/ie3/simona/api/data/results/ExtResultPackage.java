package edu.ie3.simona.api.data.results;

import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.system.SystemParticipantResult;
import edu.ie3.simona.api.data.ExtDataPackage;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Dimensionless;
import java.util.Map;

import static edu.ie3.util.quantities.PowerSystemUnits.PU;

public class ExtResultPackage implements ExtDataPackage {

    private final Long tick;
    private final Map<String, ResultEntity> simonaResultsMap;

    public ExtResultPackage(
            Long tick,
            Map<String, ResultEntity> simonaResultsMap) {
        this.tick = tick;
        this.simonaResultsMap = simonaResultsMap;
    }

    public Long getTick() {
        return tick;
    }

    public double getVoltageDeviation(String assetId) {
        if (simonaResultsMap.get(assetId) instanceof NodeResult nodeResult) {
            ComparableQuantity<Dimensionless> vMagDev = Quantities.getQuantity(0, PU);
            vMagDev = Quantities.getQuantity(0, PU)
                    .add(nodeResult.getvMag()
                            .subtract(Quantities.getQuantity(1.0, PU)));
            return vMagDev.getValue().doubleValue();
        } else {
            throw new RuntimeException("VOLTAGE DEVIATION is only available for NodeResult's!");
        }
    }

    public double getActivePower(String assetId) {
        if (simonaResultsMap.get(assetId) instanceof SystemParticipantResult systemParticipantResult) {
            return systemParticipantResult.getP().getValue().doubleValue();
        } else {
            throw new RuntimeException("ACTIVE POWER is only available for SystemParticipantResult's!");
        }
    }

    public double getReactivePower(String assetId) {
        if (simonaResultsMap.get(assetId) instanceof SystemParticipantResult systemParticipantResult) {
            return systemParticipantResult.getQ().getValue().doubleValue();
        } else {
            throw new RuntimeException("REACTIVE POWER is only available for SystemParticipantResult's!");
        }
    }

    public double getLineLoading(String assetId) {
        throw new RuntimeException("LINE LOADING is not implemented yet!");
    }

    public String toString() {
        return simonaResultsMap.toString();
    }
}
