package eu.cloudtm.autonomicManager.commons.dto;

import eu.cloudtm.autonomicManager.commons.Forecaster;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/17/13
 */
public class WhatIfDTO {

    private Forecaster forecaster;

    private Collection<Collection<Double>> throughput = new ArrayList<Collection<Double>>();
    private Collection<Collection<Double>> readResponseTime = new ArrayList<Collection<Double>>();
    private Collection<Collection<Double>> writeResponseTime = new ArrayList<Collection<Double>>();
    private Collection<Collection<Double>> abortRate = new ArrayList<Collection<Double>>();

    public WhatIfDTO(Forecaster forecaster){
         this.forecaster = forecaster;
    }

    public Forecaster getForecaster(){
        return forecaster;
    }

    public void addThroughputPoint(long x, double y){
        Collection<Double> point = new ArrayList<Double>();
        point.add((double) x);
        point.add(y);
        throughput.add(point);
    }

    public void addReadResponseTimePoint(long x, double y){
        Collection<Double> point = new ArrayList<Double>();
        point.add((double) x);
        point.add(y);
        readResponseTime.add(point);
    }

    public void addWriteResponseTimePoint(long x, double y){
        Collection<Double> point = new ArrayList<Double>();
        point.add((double) x);
        point.add(y);
        writeResponseTime.add(point);
    }

    public void addAbortRatePoint(long x, double y){
        Collection<Double> point = new ArrayList<Double>();
        point.add((double) x);
        point.add(y);
        abortRate.add(point);
    }

}
