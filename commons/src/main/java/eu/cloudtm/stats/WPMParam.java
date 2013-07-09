package eu.cloudtm.stats;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/8/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public enum WPMParam {

    LocalReadOnlyTxLocalServiceTime             ( 0, "LocalReadOnlyTxLocalServiceTime"),
    LocalUpdateTxLocalServiceTime               ( 1, "LocalUpdateTxLocalServiceTime"),
    RetryWritePercentage                        ( 2, "RetryWritePercentage"),
    SuxNumPuts                                  ( 3, "SuxNumPuts"),
    PrepareCommandBytes                         ( 4, "PrepareCommandBytes"),
    RTT                                         ( 5, "RTT"),
    CommitBroadcastWallClockTime                ( 6, "CommitBroadcastWallClockTime"),
    PaoloLocalTakenLocks                        ( 7, "PaoloLocalTakenLocks"),
    PaoloRemoteTakenLocks                       ( 8, "PaoloRemoteTakenLocks"),
    NumPuts                                     ( 9, "NumPuts"),
    PaoloLocalTakenHoldTime                     ( 10, "PaoloLocalTakenHoldTime"),
    PaoloRemoteTakenHoldTime                    ( 11, "PaoloRemoteTakenHoldTime");

    private final int id;
    private final String param;

    private WPMParam(int id, String name){
        this.id = id;
        this.param = name;
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



}
