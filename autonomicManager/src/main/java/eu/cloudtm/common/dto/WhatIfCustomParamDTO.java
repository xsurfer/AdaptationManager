package eu.cloudtm.common.dto;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/17/13
 */
public class WhatIfCustomParamDTO {

    private double ACF = -1;
    private double GetWriteTx = -1;
    private double GetReadOnlyTx = -1;
    private double RemoteGetLatency = -1;
    private double LocalReadOnlyTxLocalServiceTime = -1;
    private double LocalUpdateTxLocalServiceTime = -1;
    private double RetryWritePercentage = -1;
    private double SuxNumPuts = -1;
    private double PrepareCommandBytes = -1;
    private double RTT = -1;
    private double CommitBroadcastWallClockTime = -1;

    public double getACF() {
        return ACF;
    }

    public void setACF(double ACF) {
        this.ACF = ACF;
    }

    public double getGetWriteTx() {
        return GetWriteTx;
    }

    public void setGetWriteTx(double getWriteTx) {
        GetWriteTx = getWriteTx;
    }

    public double getGetReadOnlyTx() {
        return GetReadOnlyTx;
    }

    public void setGetReadOnlyTx(double getReadOnlyTx) {
        GetReadOnlyTx = getReadOnlyTx;
    }

    public double getRemoteGetLatency() {
        return RemoteGetLatency;
    }

    public void setRemoteGetLatency(double remoteGetLatency) {
        RemoteGetLatency = remoteGetLatency;
    }

    public double getLocalReadOnlyTxLocalServiceTime() {
        return LocalReadOnlyTxLocalServiceTime;
    }

    public void setLocalReadOnlyTxLocalServiceTime(double localReadOnlyTxLocalServiceTime) {
        LocalReadOnlyTxLocalServiceTime = localReadOnlyTxLocalServiceTime;
    }

    public double getLocalUpdateTxLocalServiceTime() {
        return LocalUpdateTxLocalServiceTime;
    }

    public void setLocalUpdateTxLocalServiceTime(double localUpdateTxLocalServiceTime) {
        LocalUpdateTxLocalServiceTime = localUpdateTxLocalServiceTime;
    }

    public double getRetryWritePercentage() {
        return RetryWritePercentage;
    }

    public void setRetryWritePercentage(double retryWritePercentage) {
        RetryWritePercentage = retryWritePercentage;
    }

    public double getSuxNumPuts() {
        return SuxNumPuts;
    }

    public void setSuxNumPuts(double suxNumPuts) {
        SuxNumPuts = suxNumPuts;
    }

    public double getPrepareCommandBytes() {
        return PrepareCommandBytes;
    }

    public void setPrepareCommandBytes(double prepareCommandBytes) {
        PrepareCommandBytes = prepareCommandBytes;
    }

    public double getRTT() {
        return RTT;
    }

    public void setRTT(double RTT) {
        this.RTT = RTT;
    }

    public double getCommitBroadcastWallClockTime() {
        return CommitBroadcastWallClockTime;
    }

    public void setCommitBroadcastWallClockTime(double commitBroadcastWallClockTime) {
        CommitBroadcastWallClockTime = commitBroadcastWallClockTime;
    }

}
