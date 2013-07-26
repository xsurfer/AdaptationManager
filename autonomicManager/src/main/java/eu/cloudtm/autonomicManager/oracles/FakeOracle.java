package eu.cloudtm.autonomicManager.oracles;

import eu.cloudtm.commons.ForecastParam;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.commons.ReplicationProtocol;
import eu.cloudtm.oracles.InputOracle;
import eu.cloudtm.oracles.Oracle;
import eu.cloudtm.oracles.OutputOracle;
import eu.cloudtm.oracles.exceptions.OracleException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/24/13
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class FakeOracle implements Oracle {

    private static AtomicInteger partialCounter = new AtomicInteger(0);
    private static AtomicInteger counter = new AtomicInteger(0);

    private Random rnd = new Random();

    private Map<Integer, PlatformConfiguration> counter2plat = new HashMap<Integer, PlatformConfiguration>(){{
        put(0,  new PlatformConfiguration(2,  2, ReplicationProtocol.TO ) );        // full
        put(1,  new PlatformConfiguration(4,  4, ReplicationProtocol.TWOPC ) );     // full
        put(2, new PlatformConfiguration(6,  3, ReplicationProtocol.TO ) );         // 50%
        put(3, new PlatformConfiguration(8,  2, ReplicationProtocol.TO ) );         // 25%
        put(4, new PlatformConfiguration(10, 2, ReplicationProtocol.TWOPC ) );      // 20%
    }};


    @Override
    public OutputOracle forecast(InputOracle input) throws OracleException {

        PlatformConfiguration toApply = counter2plat.get( getCurr() );

        boolean scaleBool = (toApply.platformSize() == (Integer) input.getForecastParam(ForecastParam.NumNodes))? true : false;
        boolean degreeBool = (toApply.replicationDegree() == (Integer) input.getForecastParam(ForecastParam.ReplicationDegree))? true : false;
        boolean protocolBool = (toApply.replicationProtocol() == (ReplicationProtocol) input.getForecastParam(ForecastParam.ReplicationProtocol))? true : false;

        OutputOracle toReturn;
        if(scaleBool && degreeBool && protocolBool){
            //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            toReturn = new OutputOracleImpl(1000, rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble() );
        } else {
            toReturn = new OutputOracleImpl(0, rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble() );
        }

        if( partialCounter.incrementAndGet() >= 135 ){
            partialCounter.set(0);
            counter.incrementAndGet();
            //System.out.println("incrementing curr to:" + getCurr() );
        } else {
            //System.out.println("curr: " + getCurr());
        }
        return toReturn;
    }

    private int getCurr(){
        return counter.get() % 5;
    }
}
