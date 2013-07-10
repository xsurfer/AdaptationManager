package eu.cloudtm.commons.dto;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/17/13
 */
public class WhatIfDTO {

    private Collection<Collection<Double>> throughput = new ArrayList<Collection<Double>>();
    private Collection<Collection<Double>> responseTime = new ArrayList<Collection<Double>>();
    private Collection<Collection<Double>> abortRate = new ArrayList<Collection<Double>>();

    public WhatIfDTO(){

    }

    public void addThroughputPoint(long x, double y){
        Collection<Double> point = new ArrayList<Double>();
        point.add((double) x);
        point.add(y);
        throughput.add(point);
    }

    public void addResponseTimePoint(long x, double y){
        Collection<Double> point = new ArrayList<Double>();
        point.add((double) x);
        point.add(y);
        responseTime.add(point);
    }

    public void addAbortRatePoint(long x, double y){
        Collection<Double> point = new ArrayList<Double>();
        point.add((double) x);
        point.add(y);
        abortRate.add(point);
    }

}
