package eu.cloudtm.autonomicManager.commons;


import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.oracles.Oracle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/30/13
 */
public enum Forecaster {
    NONE(false, null),
    COMMITTEE(true, Config.getInstance().getString( KeyConfig.FORECASTER_COMMITTEE.key() ) ),
    ANALYTICAL( true, Config.getInstance().getString( KeyConfig.FORECASTER_ANALYTICAL.key() ) ),
    SIMULATOR( true, Config.getInstance().getString( KeyConfig.FORECASTER_SIMULATOR.key() ) ),
    MACHINE_LEARNING(true, Config.getInstance().getString( KeyConfig.FORECASTER_MACHINE_LEARNING.key() ));

    private static Log log = LogFactory.getLog(Forecaster.class);

    private final boolean autoTuning;
    private final String oracleClass;

    private Forecaster(final boolean _autoScale, String _oracleClass) {
        autoTuning = _autoScale;
        oracleClass = _oracleClass;
    }

    public boolean isAutoTuning() {
        return autoTuning;
    }

    public String getOracleClass(){
        return oracleClass;
    }

    public Oracle getInstance() {
        String oracleName = getOracleClass();
        if ( getOracleClass().indexOf('.') < 0) {
            oracleName = "eu.cloudtm.autonomicManager.oracles." + getOracleClass();
        }
        try {
            Oracle oracle;
            Constructor c = Class.forName(oracleName).getConstructor();
            oracle = (Oracle) c.newInstance();
            return oracle;
        } catch (ClassNotFoundException e) {
            log.error(e);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            log.error(e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.error(e);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            log.error(e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

}
