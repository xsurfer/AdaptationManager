package eu.cloudtm.oracles;

import eu.cloudtm.commons.KPI;
import eu.cloudtm.oracles.exceptions.OracleException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/12/13
 */
public interface IOracle {

    public KPI forecast(InputOracle input) throws OracleException;

    //public KPI minimizeCosts(InputOracleWPM input, double arrivalRate, double abortRate, double responseTime) throws OracleException;

    //public Set<KPI> whatIf(InputOracleWPM input) throws OracleException;

    //public KPI forecastWithCustomParam(WPMSample sample, WhatIfCustomParamDTO customParam, int numNodes, int numThreads) throws OracleException;

}
