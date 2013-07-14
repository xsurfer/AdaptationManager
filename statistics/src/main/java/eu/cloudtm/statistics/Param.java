package eu.cloudtm.statistics;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Param {

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
    private final String key;
    private final Class clazz;

    private Param(int id, String name, Class clazz){
        this.id = id;
        this.key = name;
        this.clazz = clazz;
    }

    public int getId(){
        return id;
    }

    public String getKey(){
        return key;
    }

    public static Param getById(int id){
        for( Param param : values() ){
            if( param.getId() == id )
                return param;
        }
        return null;
    }

    public static Param getByName(String name){
        for( Param param : values() ){
            if( param.getKey() == name )
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
