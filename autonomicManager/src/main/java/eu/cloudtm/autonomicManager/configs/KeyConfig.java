package eu.cloudtm.autonomicManager.configs;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/22/13
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public enum KeyConfig {

    ALERT_MANAGER_POLICY("alertManager.policy"),

    DELTACLOUD_URL("deltacloud.url"),
    DELTACLOUD_USER("deltacloud.user"),
    DELTACLOUD_PASSWORD("deltacloud.password"),

    DELTACLOUD_IMAGE("deltacloud.image"),
    DELTACLOUD_FLAVOR("deltacloud.flavor"),

    ISPN_JMX_PORT("infinispan.jmxPort"),
    ISPN_DOMAIN("infinispan.domain"),
    ISPN_CACHE_NAME("infinispan.cacheName"),

    ISPN_ACTUATOR_FENIX_FRAMEWORK("infinispanActuator.fenixFrameworkDomain"),
    ISPN_ACTUATOR_APPLICATION_NAME("infinispanActuator.applicationName"),

    ISPN_ACTUATOR_FORCE_STOP("infinispanActuator.forceStop"),
    ISPN_ACTUATOR_ABORT_ON_STOP("infinispanActuator.abortOnStop"),

    RADARGUN_ACTUATOR("radargun.actuator"),
    RADARGUN_COMPONENT("radargun.component"),

    ;

    private final String key;

    private KeyConfig(String key){
        this.key = key;
    }

    public String key(){
        return this.key;
    }

}
