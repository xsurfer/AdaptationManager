package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.RESTServer.RESTServer;
import eu.cloudtm.autonomicManager.actuators.CloudTMActuator;
import eu.cloudtm.autonomicManager.actuators.clients.RadargunClient;
import eu.cloudtm.autonomicManager.actuators.clients.RadargunClientJMX;
import eu.cloudtm.autonomicManager.commons.*;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.statistics.SampleProducer;
import eu.cloudtm.autonomicManager.statistics.StatsManager;
import eu.cloudtm.autonomicManager.statistics.WPMStatsManagerFactory;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzer;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzerFactory;
import org.apache.deltacloud.client.DeltaCloudClient;
import org.apache.deltacloud.client.DeltaCloudClientException;
import org.apache.deltacloud.client.DeltaCloudClientImpl;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class AutonomicManagerFactory implements AbstractAutonomicManagerFactory {

    private State platformState;
    private IReconfigurator reconfigurator;
    private PlatformTuning platformTuning;
    private PlatformConfiguration platformConfiguration;
    private AbstractOptimizer optimizer;
    private SLAManager slaManager;
    private WorkloadAnalyzer workloadAnalyzer;
    private RESTServer restServer;

    private IActuator actuator;
    private RadargunClient radargunClient;

    private StatsManager wpmStatsManager;

    private WPMStatsManagerFactory wpmStatsManagerFactory;
    //private WorkloadAnalyzerFactory workloadAnalyzerFactory;

    public AutonomicManagerFactory(){
    }


    public AutonomicManager build(){

        wpmStatsManager = new WPMStatsManagerFactory( getPlatformConfiguration() ).build();

        AutonomicManager autonomicManager = new AutonomicManager(
                getPlatformConfiguration(),
                getPlatformTuning(),
                wpmStatsManager,
                getWorkloadAnalyzer(),
                getOptimizer(),
                getReconfigurator());

        try {
            getRESTServer().startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return autonomicManager;
    }

    @Override
    public State getPlatformState() {
        if( this.platformState == null ){
            this.platformState = new State(PlatformState.RUNNING);
        }
        return this.platformState;
    }

    @Override
    public PlatformTuning getPlatformTuning() {
        if( this.platformTuning==null ) {
            this.platformTuning= new PlatformTuning(Forecaster.ANALYTICAL, true);
        }
        return this.platformTuning;
    }

    @Override
    public PlatformConfiguration getPlatformConfiguration() {
        if( this.platformConfiguration == null ){
            this.platformConfiguration = new PlatformConfiguration();
        }
        return this.platformConfiguration;
    }

    public RadargunClient getRadargunClient(){

        if(radargunClient == null){
            String actuator = Config.getInstance().getString( KeyConfig.RADARGUN_ACTUATOR.key() );

            if(actuator.equals("JMX")){
                radargunClient = new RadargunClientJMX( Config.getInstance().getString( KeyConfig.RADARGUN_COMPONENT.key() ) );
            } else {
                // TO IMPLEMENT SLAVEKILLER CLIENT
                throw new RuntimeException("TO IMPLEMENT");
            }
        }
        return radargunClient;
    }

    public DeltaCloudClient getDeltaCloudClient(){
        String hostname = Config.getInstance().getString( KeyConfig.DELTACLOUD_URL.key() );
        String username = Config.getInstance().getString( KeyConfig.DELTACLOUD_USER.key() );
        String password = Config.getInstance().getString( KeyConfig.DELTACLOUD_PASSWORD.key() );
        DeltaCloudClient deltaCloudClient;
        try {
            deltaCloudClient = new DeltaCloudClientImpl(hostname, username, password);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (DeltaCloudClientException e) {
            throw new RuntimeException(e);
        }
        return deltaCloudClient;
    }

    public IActuator getActuator(){
        if( this.actuator == null ){
            int jmxPort = Config.getInstance().getInt( KeyConfig.ISPN_JMX_PORT.key() );
            String imageId = Config.getInstance().getString( KeyConfig.DELTACLOUD_IMAGE.key() );
            String flavorId = Config.getInstance().getString( KeyConfig.DELTACLOUD_FLAVOR.key() );

            String domain = Config.getInstance().getString( KeyConfig.ISPN_DOMAIN.key() );
            String cacheName = Config.getInstance().getString( KeyConfig.ISPN_CACHE_NAME.key() );

            //this.actuator = new CloudTMActuator( getDeltaCloudClient(), jmxPort, imageId, flavorId, domain, cacheName );
            this.actuator = new CloudTMActuator( getDeltaCloudClient(), getRadargunClient(), jmxPort, imageId, flavorId, domain, cacheName ); // with radargun
        }
        return actuator;
    }

    @Override
    public IReconfigurator getReconfigurator() {
        if( this.reconfigurator == null ){
            this.reconfigurator = new Reconfigurator( getPlatformConfiguration(), getPlatformState(), getActuator());
        }
        return reconfigurator;
    }

    @Override
    public AbstractOptimizer getOptimizer() {

        if( this.optimizer == null ){
            this.optimizer = new MuleOptimizer(getReconfigurator(), getSLAManager(), getPlatformConfiguration() ,getPlatformTuning());
        }
        return this.optimizer;
    }

    @Override
    public SLAManager getSLAManager() {
        if ( this.slaManager == null ){
            this.slaManager = new SLAManager();
        }
        return this.slaManager;
    }

    @Override
    public WorkloadAnalyzer getWorkloadAnalyzer() {
        if( this.workloadAnalyzer == null ){
            WorkloadAnalyzerFactory workloadAnalyzerFactory = new WorkloadAnalyzerFactory((SampleProducer) wpmStatsManager, getReconfigurator(), getOptimizer());
            this.workloadAnalyzer = workloadAnalyzerFactory.build();
        }
        return this.workloadAnalyzer;
    }

    @Override
    public RESTServer getRESTServer() {
        if( this.restServer == null ){
            this.restServer = new RESTServer(wpmStatsManager);
        }
        return this.restServer;
    }

}