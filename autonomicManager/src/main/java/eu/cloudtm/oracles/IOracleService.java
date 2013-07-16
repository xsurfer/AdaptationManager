package eu.cloudtm.oracles;

import eu.cloudtm.commons.KPI;
import eu.cloudtm.commons.PlatformConfiguration;
import eu.cloudtm.oracles.exceptions.OracleException;
import eu.cloudtm.statistics.ProcessedSample;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/15/13
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IOracleService {

    public PlatformConfiguration minimizeCosts(ProcessedSample sample,
                                               double arrivalRateToGuarantee,
                                               double abortRateToGuarantee,
                                               double responseTimeToGuarantee) throws OracleException;


    public Map<PlatformConfiguration, KPI> whatIf(ProcessedSample sample);


}
