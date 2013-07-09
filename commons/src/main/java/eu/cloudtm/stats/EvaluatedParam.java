package eu.cloudtm.stats;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/9/13
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public enum EvaluatedParam {


    ACF             ( 0, "ACF" );

    private final int id;
    private final String param;

    private EvaluatedParam(int id, String name){
        this.id = id;
        this.param = name;
    }

    public int getId(){
        return id;
    }

    public String getParam(){
        return param;
    }

    public static EvaluatedParam getById(int id){
        for( EvaluatedParam param : values() ){
            if( param.getId() == id )
                return param;
        }
        return null;
    }


}
