package eu.cloudtm.autonomicManager;

import eu.cloudtm.commons.*;
import eu.cloudtm.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.commons.dto.WhatIfDTO;
import eu.cloudtm.oracles.OutputOracle;
import eu.cloudtm.statistics.ProcessedSample;
import eu.cloudtm.statistics.StatsManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/10/13
 */
public class AutonomicManager {

    private static Log log = LogFactory.getLog(AutonomicManager.class);

    private PlatformConfiguration platformConfiguration;
    private StatsManager statsManager;
    private InputFilter inputFilter;
    private Optimizer optimizer;
    private Reconfigurator reconfigurator;

    public AutonomicManager(PlatformConfiguration platformConfiguration,
                            PlatformTuning platformTuning,
                            StatsManager sampleManager,
                            InputFilter inputFilter,
                            Optimizer optimizer,
                            Reconfigurator reconfigurator){
        this.statsManager = sampleManager;
        this.platformConfiguration = platformConfiguration;

        this.inputFilter = inputFilter;
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
            log.info("4 - WhatIf");
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
                break;
            case 4:
                whatIf();
                break;
            default:
                log.info("Unexpected input");
                break;
        }
    }

    private void whatIf(){

        ProcessedSample processedSample = statsManager.getLastSample();
        if(processedSample==null) {
            log.warn("No sample in the StatsManager");
            return;
        }

        WhatIf whatIf = new WhatIf(processedSample);

        WhatIfCustomParamDTO customParamDTO = new WhatIfCustomParamDTO();
        customParamDTO.addForecaster(Forecaster.ANALYTICAL);
        customParamDTO.setReplicationProtocol(ReplicationProtocol.TWOPC);
        customParamDTO.setReplicationDegree(4);


        List<WhatIfDTO> result = whatIf.evaluate(customParamDTO);

        /* Stampa delle predizioni */
        for (WhatIfDTO whatIfRes : result){
            log.info("Forecaster: " + whatIfRes.getForecaster());
            log.info("to reimplement");

        }
    }

    private void currentConfiguration(){
        log.info("*** Current Configuration ***");
        log.info("Scale: " + platformConfiguration.platformSize() + " " + platformConfiguration.nodeConfiguration());
        log.info("RepDegree: " + platformConfiguration.replicationDegree());
        log.info("RepProtocol: " + platformConfiguration.replicationProtocol());
        log.info("");
    }


}
