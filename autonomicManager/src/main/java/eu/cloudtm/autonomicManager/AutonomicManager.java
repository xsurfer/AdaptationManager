package eu.cloudtm.autonomicManager;

import com.google.gson.Gson;
import eu.cloudtm.autonomicManager.commons.*;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfDTO;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import eu.cloudtm.autonomicManager.oracles.exceptions.OracleException;
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

    private PlatformTuning platformTuning;
    private PlatformConfiguration platformConfiguration;
    private StatsManager statsManager;
    private WorkloadAnalyzer workloadAnalyzer;
    private Optimizer optimizer;
    private Reconfigurator reconfigurator;


    public AutonomicManager(PlatformConfiguration platformConfiguration,
                            PlatformTuning platformTuning,
                            StatsManager sampleManager,
                            WorkloadAnalyzer workloadAnalyzer,
                            Optimizer optimizer,
                            Reconfigurator reconfigurator){
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

    public void menu(){
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

    public void processInput(int selection){
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

        /* adding whatIf options */
        customParamDTO.addForecaster(Forecaster.ANALYTICAL);
        customParamDTO.setReplicationProtocol(ReplicationProtocol.TWOPC);
        customParamDTO.setReplicationDegree(2);

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


}
