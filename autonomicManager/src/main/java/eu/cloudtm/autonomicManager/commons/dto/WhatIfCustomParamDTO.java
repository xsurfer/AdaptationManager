package eu.cloudtm.autonomicManager.commons.dto;

import eu.cloudtm.autonomicManager.commons.Forecaster;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/17/13
 */
public class WhatIfCustomParamDTO {

    public enum Xaxis {
        NODES, DEGREE, PROTOCOL
    }

    private Xaxis xaxis = Xaxis.NODES;

    private int fixedDegreeMin = 2;
    private int fixedDegreeMax = 10;

    private int fixedNodesMin = 2;
    private int fixedNodesMax = 10;

    private ReplicationProtocol fixedProtocol = ReplicationProtocol.TWOPC;

    private Set<Forecaster> forecasters = new HashSet<Forecaster>();

    private double acf = -1D;
    private double avgGetsPerWrTransaction = -1; //  # GET per write transaction
    private long avgGetsPerROTransaction = -1L;   // #GET per read only transaction
    private long avgRemoteGetRtt = -1L;  //  Remote get latency
    private long localReadOnlyTxLocalServiceTime = -1L; // Read Only transaction demand
    private long localUpdateTxLocalServiceTime = -1L;  // Write transaction demand
    private double percentageSuccessWriteTransactions = -1D;  // Write Transactions Percentage
    private double avgNumPutsBySuccessfulLocalTx = -1D;  // #put per write tx
    private long avgPrepareCommandSize = -1L; // size of prepare msg
    private long avgPrepareAsync = -1L;   // prepare latency
    private long avgCommitAsync = -1L;    // commit latency

    public Xaxis getXaxis() {
        return xaxis;
    }

    public void setXaxis(Xaxis xaxis) {
        this.xaxis = xaxis;
    }


    /* *** NODES *** */

    public Integer getFixedNodesMin() {
        return fixedNodesMin;
    }

    public void setFixedNodesMin(int fixedNodesMin) {
        if(fixedNodesMin <= 0)
            throw new IllegalArgumentException("fixedNodesMin must be > 0");
        this.fixedNodesMin = fixedNodesMin;
    }

    public Integer getFixedNodesMax() {
        return fixedNodesMax;
    }

    public void setFixedNodesMax(int fixedNodesMax) {
        if(fixedNodesMax <= 0 || fixedNodesMax < fixedNodesMin)
            throw new IllegalArgumentException("Nodes must be > 0 && >= min nodes");
        this.fixedNodesMax = fixedNodesMax;
    }


    /* *** DEGREE *** */

    public void setFixedDegreeMin(int fixedDegreeMin){
        if(fixedDegreeMin <= 0)
            throw new IllegalArgumentException("fixedDegreeMin must be > 0");
        this.fixedDegreeMin = fixedDegreeMin;
    }

    public int getFixedDegreeMin(){
        return fixedDegreeMin;
    }

    public void setFixedDegreeMax(int fixedDegreeMax) {
        if(fixedDegreeMax <= 0 || fixedDegreeMax < fixedDegreeMin)
            throw new IllegalArgumentException("fixedDegreeMax must be > 0 && >= fixedDegreeMin");
        this.fixedDegreeMax = fixedDegreeMax;
    }

    public Integer getFixedDegreeMax() {
        return fixedDegreeMax;
    }


    /* *** PROTOCOL *** */

    public void setFixedProtocol(ReplicationProtocol repProt){
        this.fixedProtocol = repProt;
    }

    public ReplicationProtocol getFixedProtocol(){
        return fixedProtocol;
    }

    /* *** OTHERS *** */

    public void addForecaster(Forecaster forecaster){
        forecasters.add(forecaster);
    }

    public boolean removeForecaster(Forecaster forecaster){
        return forecasters.remove(forecaster);
    }

    public Set<Forecaster> getForecasters(){
        return forecasters;
    }

    public double getACF() {
        return acf;
    }

    public void setACF(double acf) {
        this.acf = acf;
    }

    public double getAvgGetsPerWrTransaction() {
        return avgGetsPerWrTransaction;
    }

    public void setAvgGetsPerWrTransaction(double avgGetsPerWrTransaction) {
        this.avgGetsPerWrTransaction = avgGetsPerWrTransaction;
    }

    public double getAvgGetsPerROTransaction() {
        return avgGetsPerROTransaction;
    }

    public void setAvgGetsPerROTransaction(long avgGetsPerROTransaction) {
        this.avgGetsPerROTransaction = avgGetsPerROTransaction;
    }

    public double getAvgRemoteGetRtt() {
        return avgRemoteGetRtt;
    }

    public void setAvgRemoteGetRtt(long avgRemoteGetRtt) {
        this.avgRemoteGetRtt = avgRemoteGetRtt;
    }

    public Long getLocalReadOnlyTxLocalServiceTime() {
        return localReadOnlyTxLocalServiceTime;
    }

    public void setLocalReadOnlyTxLocalServiceTime(long localReadOnlyTxLocalServiceTime) {
        this.localReadOnlyTxLocalServiceTime = localReadOnlyTxLocalServiceTime;
    }

    public Long getLocalUpdateTxLocalServiceTime() {
        return localUpdateTxLocalServiceTime;
    }

    public void setLocalUpdateTxLocalServiceTime(long localUpdateTxLocalServiceTime) {
        this.localUpdateTxLocalServiceTime = localUpdateTxLocalServiceTime;
    }

    public Double getPercentageSuccessWriteTransactions() {
        return percentageSuccessWriteTransactions;
    }

    public void setPercentageSuccessWriteTransactions(double percentageSuccessWriteTransactions) {
        this.percentageSuccessWriteTransactions = percentageSuccessWriteTransactions;
    }

    public Double getAvgNumPutsBySuccessfulLocalTx() {
        return avgNumPutsBySuccessfulLocalTx;
    }

    public void setAvgNumPutsBySuccessfulLocalTx(double avgNumPutsBySuccessfulLocalTx) {
        this.avgNumPutsBySuccessfulLocalTx = avgNumPutsBySuccessfulLocalTx;
    }

    public Long getAvgPrepareCommandSize() {
        return avgPrepareCommandSize;
    }

    public void setAvgPrepareCommandSize(long avgPrepareCommandSize) {
        this.avgPrepareCommandSize = avgPrepareCommandSize;
    }

    public double getAvgPrepareAsync() {
        return avgPrepareAsync;
    }

    public void setAvgPrepareAsync(long avgPrepareAsync) {
        this.avgPrepareAsync = avgPrepareAsync;
    }

    public Long getAvgCommitAsync() {
        return avgCommitAsync;
    }

    public void setAvgCommitAsync(long avgCommitAsync) {
        this.avgCommitAsync = avgCommitAsync;
    }

}
