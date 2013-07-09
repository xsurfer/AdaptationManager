package eu.cloudtm;

import eu.cloudtm.common.dto.WhatIfCustomParamDTO;
import eu.cloudtm.exceptions.OracleException;
import eu.cloudtm.model.KPI;
import eu.cloudtm.oracles.InputOracle;
import eu.cloudtm.stats.WPMSample;

import java.util.Set;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/12/13
 */
public interface IOracle {

    //public KPI minimizeCosts(InputOracle input, double arrivalRate, double abortRate, double responseTime) throws OracleException;

    //public Set<KPI> whatIf(InputOracle input) throws OracleException;

    public KPI forecast(InputOracle input) throws OracleException;

//    public KPI forecastWithCustomParam(WPMSample sample, WhatIfCustomParamDTO customParam, int numNodes, int numThreads) throws OracleException;

}
