package eu.cloudtm.statistics;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public enum WPMParam {

    LocalReadOnlyTxLocalServiceTime             ( 0, "LocalReadOnlyTxLocalServiceTime", Double.class),
    LocalUpdateTxLocalServiceTime               ( 1, "LocalUpdateTxLocalServiceTime", Double.class),
    RetryWritePercentage                        ( 2, "RetryWritePercentage", Double.class),
    SuxNumPuts                                  ( 3, "SuxNumPuts", Double.class),
    PrepareCommandBytes                         ( 4, "PrepareCommandBytes", Double.class),
    RTT                                         ( 5, "RTT", Double.class),
    CommitBroadcastWallClockTime                ( 6, "CommitBroadcastWallClockTime", Double.class),
    PaoloLocalTakenLocks                        ( 7, "PaoloLocalTakenLocks", Double.class),
    PaoloRemoteTakenLocks                       ( 8, "PaoloRemoteTakenLocks", Double.class),
    NumPuts                                     ( 9, "NumPuts", Double.class),
    PaoloLocalTakenHoldTime                     ( 10, "PaoloLocalTakenHoldTime", Double.class),
    PaoloRemoteTakenHoldTime                    ( 11, "PaoloRemoteTakenHoldTime", Double.class),
    CurrentProtocolId                           ( 12, "CurrentProtocolId", String.class),
    CommitProbability                           ( 13, "CommitProbability", Double.class),
    Throughput                                  ( 14, "Throughput", Double.class),



    ;

    private final int id;
    private final String param;
    private final Class clazz;

    private WPMParam(int id, String name, Class clazz){
        this.id = id;
        this.param = name;
        this.clazz = clazz;
    }

    public int getId(){
        return id;
    }

    public String getParam(){
        return param;
    }

    public static WPMParam getById(int id){
        for( WPMParam param : values() ){
            if( param.getId() == id )
                return param;
        }
        return null;
    }

    public static WPMParam getByName(String name){
        for( WPMParam param : values() ){
            if( param.getParam() == name )
                return param;
        }
        return null;
    }

    public <T> T castTo(Object val, Class<T> clz){
        return clz.cast(val);
    }

    public Class getClazz(){
        return clazz;
    }
}
