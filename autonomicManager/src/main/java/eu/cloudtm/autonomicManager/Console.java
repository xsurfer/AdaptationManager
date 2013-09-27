package eu.cloudtm.autonomicManager;

import com.google.gson.Gson;
import eu.cloudtm.autonomicManager.commons.Forecaster;
import eu.cloudtm.autonomicManager.commons.InstanceConfig;
import eu.cloudtm.autonomicManager.commons.PlatformConfiguration;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfCustomParamDTO;
import eu.cloudtm.autonomicManager.commons.dto.WhatIfDTO;
import eu.cloudtm.autonomicManager.optimizers.OptimizerType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 9/3/13
 */
public class Console {

    private static Log log = LogFactory.getLog(Console.class);
    private final AutonomicManager autonomicManager;

    public Console(AutonomicManager autonomicManager){
        this.autonomicManager = autonomicManager;
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
            log.info("6 - Enable/Disable WorkloadAnalyzer [ current: " + (autonomicManager.isWorkloadAnalyzerEnabled() ? "enabled" : "disabled")  + " ]");
            log.info("7 - Forecast (w/o reconfigure)");
            log.info("8 - Change forecaster");
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
            case 7:
                forecast();
                break;
            case 8:
                changeForecaster();
                break;
            default:
                log.info("Unexpected input");
                break;
        }
    }

    private void currentConfiguration(){
        log.info("*** Current Configuration ***");
        log.info( autonomicManager.platformConfiguration() );
        log.info("");
    }

    private void changeForecaster(){

        try {
            Scanner in = new Scanner(System.in);

            log.info("Forecaster: [ ANALYTICAL | SIMULATOR | MACHINE_LEARNING | COMMITTEE ] ");
            String forecasterStr = in.next();
            Forecaster forecaster = Forecaster.valueOf(forecasterStr);
            autonomicManager.updateForecaster(forecaster);

        } catch (IllegalArgumentException e){
            log.info("Illegal value for replication protocol!");
        }

    }

    private void customConfiguration(){

        PlatformConfiguration platformConfiguration = new PlatformConfiguration();

        try {
            Scanner in = new Scanner(System.in);

            log.info("Num. Nodes: ");
            int nodes = in.nextInt();
            platformConfiguration.setPlatformScale(nodes, InstanceConfig.MEDIUM);


            log.info("Replication Degree: ");
            int degree = in.nextInt();
            platformConfiguration.setRepDegree(degree);

            log.info("Protocol: [ TWOPC | TO | PB ] ");
            String protocolString = in.next();
            ReplicationProtocol protocol = ReplicationProtocol.valueOf(protocolString);
            platformConfiguration.setRepProtocol(protocol);

            Map<OptimizerType, Object> configuration = new HashMap<OptimizerType, Object>();
            configuration.put(OptimizerType.PLATFORM, platformConfiguration);
            configuration.put(OptimizerType.AUTOPLACER, null);

            autonomicManager.customConfiguration(configuration);

        } catch (IllegalArgumentException e){
            log.info("Illegal value for replication protocol!");
        }

    }

    private void forecast(){
        Gson gson = new Gson();

        log.info( gson.toJson(autonomicManager.forecast() ) );

    }

    private void switchWorkloadAnalyzer(){
        autonomicManager.switchWorkloadAnalyzer();
    }

    private void optimizeNow(){
        autonomicManager.optimizeAndReconfigureNow();
    }

    private void whatIf(){

        WhatIfCustomParamDTO customParamDTO = new WhatIfCustomParamDTO();

        /* reading and setting whatIf options */
        Scanner in = new Scanner(System.in);

        /* forecaster */
        log.info("Oracle: [ ANALYTICAL | SIMULATOR | MACHINE_LEARNING | COMMITTEE ] ");
        String forecasterStr = in.next();
        Forecaster forecaster = Forecaster.valueOf(forecasterStr);
        customParamDTO.addForecaster(forecaster);

        log.info("X-axis: [ NODES | DEGREE | PROTOCOL ]");
        String xaxisString = in.next();
        WhatIfCustomParamDTO.Xaxis xaxis = WhatIfCustomParamDTO.Xaxis.valueOf(xaxisString);
        customParamDTO.setXaxis(xaxis);


        switch (xaxis){
            case NODES:
                log.info("minNodes: [2-10]");
                int min = in.nextInt();
                customParamDTO.setFixedNodesMin(min);

                log.info("maxNodes: [" + min + "-10]");
                int max = in.nextInt();
                customParamDTO.setFixedNodesMax(max);

                /* replication degree */
                log.info("Fixed Rep. Degree: ");
                int repDegree = in.nextInt();
                customParamDTO.setFixedDegreeMax(repDegree);

                /* replication protocol */
                log.info("Replication Protocol: [ TWOPC | TO | PB ] ");
                String replicationProtocolStr = in.next();
                ReplicationProtocol replicationProtocol = ReplicationProtocol.valueOf(replicationProtocolStr);
                customParamDTO.setFixedProtocol(replicationProtocol);

                break;
            case DEGREE:
                log.info("Fixed Num. nodes: [2-10]");
                int nodes = in.nextInt();
                customParamDTO.setFixedNodesMax(nodes);

                log.info("minDegree: [2-" + nodes + "]");
                min = in.nextInt();
                customParamDTO.setFixedDegreeMin(min);

                log.info("maxDegree: [" + min + "-" + nodes + "]");
                max = in.nextInt();
                customParamDTO.setFixedDegreeMax(max);

                /* replication protocol */
                log.info("Replication Protocol: [ TWOPC | TO | PB ] ");
                replicationProtocolStr = in.next();
                replicationProtocol = ReplicationProtocol.valueOf(replicationProtocolStr);
                customParamDTO.setFixedProtocol(replicationProtocol);

                break;
            case PROTOCOL:
                log.info("Fixed num. nodes: [2-10]");
                nodes = in.nextInt();
                customParamDTO.setFixedNodesMax(nodes);

                log.info("Fixed Rep. Degree: ");
                repDegree = in.nextInt();
                customParamDTO.setFixedDegreeMax(repDegree);

                break;
            default:
                throw new IllegalArgumentException("Not valid X-axis param");
        }

        log.info("Executing what-if analysis with: [ " +
                "X-axis: " + customParamDTO.getXaxis() + ", " +
                "minNodes: " + customParamDTO.getFixedNodesMin() + ", " +
                "minNodes: " + customParamDTO.getFixedNodesMax() + ", " +
                "fixedDegree: " + customParamDTO.getFixedDegreeMax() + ", " +
                "fixedProtocol: " + customParamDTO.getFixedProtocol() + "] "
        );
        List<WhatIfDTO> result = autonomicManager.whatIf(customParamDTO);

        Gson gson = new Gson();
        String json = gson.toJson(result);

        log.trace("RESULT: " + json);

    }


}
