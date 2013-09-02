package eu.cloudtm.autonomicManager;

import com.google.gson.Gson;
import eu.cloudtm.autonomicManager.commons.*;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfDTO;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import eu.cloudtm.autonomicManager.statistics.StatsManager;
import eu.cloudtm.autonomicManager.workloadAnalyzer.WorkloadAnalyzer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * User: Fabio Perfetti perfabio87 [at] gmail.com
 * Date: 7/10/13
 */
public class AutonomicManager {

    private static Log log = LogFactory.getLog(AutonomicManager.class);

    private State state;
    private PlatformTuning platformTuning;
    private PlatformConfiguration platformConfiguration;
    private StatsManager statsManager;
    private WorkloadAnalyzer workloadAnalyzer;
    private Optimizer optimizer;
    private Reconfigurator reconfigurator;


    public AutonomicManager(State state,
                            PlatformConfiguration platformConfiguration,
                            PlatformTuning platformTuning,
                            StatsManager sampleManager,
                            WorkloadAnalyzer workloadAnalyzer,
                            Optimizer optimizer,
                            Reconfigurator reconfigurator){
        this.state = state;
        this.statsManager = sampleManager;
        this.platformConfiguration = platformConfiguration;
        this.platformTuning = platformTuning;

        this.workloadAnalyzer = workloadAnalyzer;
        this.optimizer = optimizer;
        this.reconfigurator = reconfigurator;
    }

    public void start(){
        log.info("Autonomic Manager started");
        log.info("Waiting for samples");
        menu();
        System.exit(0);
    }

    private void menu(){
        int selected = -1;
        while (selected!=0){
            log.info("Actions:");
            log.info("0 - Exit");
            log.info("1 - Prints current platform configuration");
            log.info("2 - Prints current tuning configurations");
            log.info("3 - Change configurations");
            log.info("4 - WhatIfService");
            log.info("5 - Optimize now");
            log.info("6 - Enable/Disable WorkloadAnalyzer [ current: " + (workloadAnalyzer.isEnabled() ? "enabled" : "disabled")  + " ]");
            Scanner in = new Scanner(System.in);
            selected = in.nextInt();
            processInput(selected);
        }
    }

    private void processInput(int selection){
        switch (selection){
            case 0:
                break;
            case 1:
                currentConfiguration();
                break;
            case 2:
                break;
            case 3:
                customConfiguration();
                break;
            case 4:
                whatIf();
                break;
            case 5:
                optimizeNow();
                break;
            case 6:
                switchWorkloadAnalyzer();
                break;
            default:
                log.info("Unexpected input");
                break;
        }
    }

    private void customConfiguration(){

        PlatformConfiguration platformConfiguration = new PlatformConfiguration();

        try {
            Scanner in = new Scanner(System.in);

            log.info("Nodes: ");
            int nodes = in.nextInt();
            platformConfiguration.setPlatformScale(nodes, InstanceConfig.MEDIUM);


            log.info("Degree: ");
            int degree = in.nextInt();
            platformConfiguration.setRepDegree(degree);

            log.info("Protocol {2PC, TO, PB}: ");
            String protocolString = in.next();
            ReplicationProtocol protocol = ReplicationProtocol.valueOf(protocolString);
            platformConfiguration.setRepProtocol(protocol);

        } catch (IllegalArgumentException e){
            log.info("Illegal value for replication protocol!");
            return;
        }

        Map<OptimizerType, Object> configuration = new HashMap<OptimizerType, Object>();
        configuration.put(OptimizerType.PLATFORM, platformConfiguration);
        configuration.put(OptimizerType.LARD, null);

        reconfigurator.reconfigure( configuration );
    }

    private void switchWorkloadAnalyzer(){
        workloadAnalyzer.enable( !workloadAnalyzer.isEnabled() );
    }

    private void optimizeNow(){
            optimizer.optimize( statsManager.getLastSample() );
    }

    private void whatIf(){

        ProcessedSample processedSample = statsManager.getLastSample();
        if(processedSample==null) {
            log.warn("No sample in the StatsManager");
            return;
        }

        WhatIfService whatIfService = new WhatIfService(processedSample);

        /* retrieving current values */
        WhatIfCustomParamDTO customParamDTO = whatIfService.retrieveCurrentValues();

        /* reading and setting whatIf options */
        Scanner in = new Scanner(System.in);

        /* forecaster */
        log.info("Oracle {ANALYTICAL, SIMULATOR, MACHINE_LEARNING}: ");
        String forecasterStr = in.next();
        Forecaster forecaster = Forecaster.valueOf(forecasterStr);
        customParamDTO.addForecaster(forecaster);

        /* replication protocol */
        log.info("Replication Protocol {TWOPC, TO, PB}: ");
        String replicationProtocolStr = in.next();
        ReplicationProtocol replicationProtocol = ReplicationProtocol.valueOf(replicationProtocolStr);
        customParamDTO.setFixedProtocol(replicationProtocol);

        /* replication degree */
        log.info("Replication Degree: ");
        int repDegree = in.nextInt();
        customParamDTO.setFixedDegree(repDegree);

        List<WhatIfDTO> result = whatIfService.evaluate(customParamDTO);

//        /* Stampa delle predizioni */
//        for (WhatIfDTO whatIfRes : result){
//            log.info("Forecaster: " + whatIfRes.getForecaster());
//            log.info("to reimplement");
//
//        }

        Gson gson = new Gson();
        String json = gson.toJson(result);

        log.trace("RESULT: " + json);

    }

    private void currentConfiguration(){
        log.info("*** Current Configuration ***");
        log.info( platformConfiguration.toString() );
        log.info("");
    }

    /**
     * Returns a copy of current PlatformTuning
     * @return a copy of current PlatformTuning
     */
    public PlatformTuning platformTuning(){
        log.trace("Cloning platformTuning...");
        return platformTuning.cloneThroughJson();
    }

    /**
     * Returns a copy of current PlatformConfiguration
     * @return a copy of current PlatformConfiguration
     */
    public PlatformConfiguration platformConfiguration(){
        log.trace("Cloning platformConfiguration...");
        return platformConfiguration.cloneThroughJson();
    }

    /**
     * Returns a copy of current State
     * @return a copy of current State
     */
    public State state(){
        log.trace("Cloning state...");
        return state.cloneThroughJson();
    }

    public void updateForecaster(Forecaster forecaster){
        log.info("Updating forecaster from:" + platformTuning().forecaster() + " to " + forecaster);
        platformTuning.setForecaster(forecaster);
    }

    public void updateScale(boolean tuning, int size, InstanceConfig instanceConfig){
        log.info("Updating scale");

        platformTuning.autoScale(tuning);
        if(!tuning){
            log.info("Triggering reconfiguration (" + size + ")");
            platformConfiguration.setPlatformScale(size, instanceConfig);
        }

    }

    public void updateDegree(boolean tuning, int degree){
        log.info("Updating degree");

        platformTuning.autoDegree(tuning);
        if(!tuning){
            log.info("Triggering reconfiguration (" + degree + ")");
            platformConfiguration.setRepDegree(degree);
        }
    }

    public void updateProtocol(boolean tuning, ReplicationProtocol protocol){
        log.info("Updating protocol");

        platformTuning.autoProtocol(tuning);
        if(!tuning){
            log.info("Triggering reconfiguration (" + protocol + ")");
            platformConfiguration.setRepProtocol(protocol);
        }
    }

    }
