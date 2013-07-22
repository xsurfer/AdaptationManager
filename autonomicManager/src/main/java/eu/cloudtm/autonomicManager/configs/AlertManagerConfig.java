package eu.cloudtm.autonomicManager.configs;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/22/13
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AlertManagerConfig {

    POLICY("alertManager.policy"),
    ;

    private final String key;

    private AlertManagerConfig(String key){
        this.key = key;
    }

    public String key(){
        return this.key;
    }

}
