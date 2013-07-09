package eu.cloudtm.model.utils;

import eu.cloudtm.oracles.Morpher;
import eu.cloudtm.oracles.OracleTAS;
import eu.cloudtm.oracles.Simulator;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/30/13
 */
public enum Forecaster {
    NONE(false, null),
    ANALYTICAL(true, OracleTAS.class),
    SIMULATOR(true, Simulator.class),
    MACHINE_LEARNING(true, Morpher.class);

    private final boolean autoTuning;
    private final Class oracleClass;

    private Forecaster(final boolean _autoScale, Class _oracleClass) {
        autoTuning = _autoScale;
        oracleClass = _oracleClass;
    }

    public boolean isAutoTuning() {
        return autoTuning;
    }

    public Class getOracleClass(){
        return oracleClass;
    }
}
