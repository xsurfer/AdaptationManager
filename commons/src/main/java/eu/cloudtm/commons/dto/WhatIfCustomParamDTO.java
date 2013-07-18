package eu.cloudtm.commons.dto;

import eu.cloudtm.commons.Forecaster;
import eu.cloudtm.commons.ReplicationProtocol;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/17/13
 */
public class WhatIfCustomParamDTO {

    private Integer replicationDegree = 2;
    private Set<Forecaster> forecasters = new HashSet<Forecaster>();
    private ReplicationProtocol replProtocol = ReplicationProtocol.TWOPC;

    private Double ACF = -1D;
    private double AvgGetsPerWrTransaction = -1; //  # GET per write transaction
    private Long AvgGetsPerROTransaction = -1L;   // #GET per read only transaction
    private Long AvgRemoteGetRtt = -1L;  //  Remote get latency
    private Long LocalReadOnlyTxLocalServiceTime = -1L; // Read Only transaction demand
    private Long LocalUpdateTxLocalServiceTime = -1L;  // Write transaction demand
    private Double PercentageSuccessWriteTransactions = -1D;  // Write Transactions Percentage
    private Double AvgNumPutsBySuccessfulLocalTx = -1D;  // #put per write tx
    private Long AvgPrepareCommandSize = -1L; // size of prepare msg
    private Long AvgPrepareAsync = -1L;   // prepare latency
    private Long AvgCommitAsync = -1L;    // commit latency

    public void setReplicationDegree(Integer val){
        if(val<=0)
            throw new IllegalArgumentException("Replication degree must be >0");
        replicationDegree = val;
    }

    public int getReplicationDegree(){
        return replicationDegree;
    }

    public void setReplicationProtocol(ReplicationProtocol repProt){
        this.replProtocol = repProt;
    }

    public ReplicationProtocol getReplicationProtocol(){
        return replProtocol;
    }

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
        return ACF;
    }

    public void setACF(double ACF) {
        this.ACF = ACF;
    }

    public double getAvgGetsPerWrTransaction() {
        return AvgGetsPerWrTransaction;
    }

    public void setAvgGetsPerWrTransaction(double avgGetsPerWrTransaction) {
        AvgGetsPerWrTransaction = avgGetsPerWrTransaction;
    }

    public double getAvgGetsPerROTransaction() {
        return AvgGetsPerROTransaction;
    }

    public void setAvgGetsPerROTransaction(Long avgGetsPerROTransaction) {
        AvgGetsPerROTransaction = avgGetsPerROTransaction;
    }

    public double getAvgRemoteGetRtt() {
        return AvgRemoteGetRtt;
    }

    public void setAvgRemoteGetRtt(Long avgRemoteGetRtt) {
        AvgRemoteGetRtt = avgRemoteGetRtt;
    }

    public Long getLocalReadOnlyTxLocalServiceTime() {
        return LocalReadOnlyTxLocalServiceTime;
    }

    public void setLocalReadOnlyTxLocalServiceTime(Long localReadOnlyTxLocalServiceTime) {
        LocalReadOnlyTxLocalServiceTime = localReadOnlyTxLocalServiceTime;
    }

    public Long getLocalUpdateTxLocalServiceTime() {
        return LocalUpdateTxLocalServiceTime;
    }

    public void setLocalUpdateTxLocalServiceTime(Long localUpdateTxLocalServiceTime) {
        LocalUpdateTxLocalServiceTime = localUpdateTxLocalServiceTime;
    }

    public Double getPercentageSuccessWriteTransactions() {
        return PercentageSuccessWriteTransactions;
    }

    public void setPercentageSuccessWriteTransactions(Double percentageSuccessWriteTransactions) {
        PercentageSuccessWriteTransactions = percentageSuccessWriteTransactions;
    }

    public Double getAvgNumPutsBySuccessfulLocalTx() {
        return AvgNumPutsBySuccessfulLocalTx;
    }

    public void setAvgNumPutsBySuccessfulLocalTx(Double avgNumPutsBySuccessfulLocalTx) {
        AvgNumPutsBySuccessfulLocalTx = avgNumPutsBySuccessfulLocalTx;
    }

    public Long getAvgPrepareCommandSize() {
        return AvgPrepareCommandSize;
    }

    public void setAvgPrepareCommandSize(Long avgPrepareCommandSize) {
        AvgPrepareCommandSize = avgPrepareCommandSize;
    }

    public double getAvgPrepareAsync() {
        return AvgPrepareAsync;
    }

    public void setAvgPrepareAsync(Long avgPrepareAsync) {
        this.AvgPrepareAsync = avgPrepareAsync;
    }

    public Long getAvgCommitAsync() {
        return AvgCommitAsync;
    }

    public void setAvgCommitAsync(Long avgCommitAsync) {
        AvgCommitAsync = avgCommitAsync;
    }




}
