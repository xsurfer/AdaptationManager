package eu.cloudtm.controller.actuators;

import eu.cloudtm.controller.IActuator;
import eu.cloudtm.controller.actuators.deltacloud.CallableSeveralTimes;
import eu.cloudtm.controller.actuators.deltacloud.DeltaCloudException;
import eu.cloudtm.controller.actuators.deltacloud.FixedExecutionRunnable;
import eu.cloudtm.controller.exceptions.ActuatorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.deltacloud.client.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/20/13
 */
public class DeltaCloudActuator implements IActuator {

    private Log log = LogFactory.getLog(DeltaCloudActuator.class);

    private final DeltaCloudClient client;

    /* DELTACLOUD INSTANCE CONFIGURATION */

    private final String prefix = "cloudtm-";

    private final String imageId = "b93d8a6e-32a0-4698-a056-6eb72c5d0613";

    private final String flavorId = "10";

    //private String imageId = "img3";


    /* CONSTANTS */

    private static final int TIME_BETWEEN_ACTION = 5; // in seconds

    private static final int MAX_CREATION_RETRIES = 10;

    private static final long MS_BETWEEN_CREATION_RETRY = 6000;

    /* ACTUATOR PARAMETERS */
    private int nodesToCreate;

    private int threadsToUse;


    public static DeltaCloudActuator getInstance(int _nodes, int threads) throws MalformedURLException, DeltaCloudClientException {
        DeltaCloudClient client = new DeltaCloudClientImpl("http://cloudtm.ist.utl.pt:30000","fabio+OpenShift","spiro55");
        //DeltaCloudClient client = new DeltaCloudClientImpl("http://localhost:3001","mockuser","mockpassword");

        DeltaCloudActuator actuator = new DeltaCloudActuator(client, _nodes, threads);

        return actuator;
    }

    private DeltaCloudActuator(DeltaCloudClient _client, int _nodes, int _threads){
        client = _client;
        setNodesToCreate(_nodes);
        threadsToUse = _threads;
    }

    public void actuate(){
        log.info(runningInstances().size() + " RUNNING instances");

        int numInstancesToChange = nodesToCreate - runningInstances().size(); //  <<< COULD BE NEGATIVE, use Math.abs() >>>

        if(numInstancesToChange>0){
            create(numInstancesToChange);

        } else if(numInstancesToChange < 0){
            delete(Math.abs(numInstancesToChange));
        } else {
            log.info("Nothing to do");
        }

        log.info("There are: " + runningInstances().size() + " instances");
    }

    private void create(int nodesToCreate){
        log.info("creating " + nodesToCreate + " instances");

        ExecutorService actionExecutor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        FixedExecutionRunnable<CallableSeveralTimes<Boolean>> runnable = new FixedExecutionRunnable(new CreatorAction(),
                nodesToCreate, TIME_BETWEEN_ACTION, TimeUnit.SECONDS, latch);

        actionExecutor.submit( runnable );
        actionExecutor.shutdown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void delete(int nodesToDelete){
        log.info("deleting " + nodesToDelete + " instances");

        ExecutorService actionExecutor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);
        FixedExecutionRunnable<CallableSeveralTimes<Boolean>> runnable = new FixedExecutionRunnable(new DestroyerAction(),
                nodesToDelete, TIME_BETWEEN_ACTION, TimeUnit.SECONDS, latch);

        actionExecutor.submit( runnable );
        actionExecutor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Instance> runningInstances(){
        List<Instance> allInstances;
        List<Instance> runningInstances = new ArrayList<Instance>();

        try {
            allInstances = client.listInstances();
        } catch (DeltaCloudClientException e) {
            throw new RuntimeException(e);
        }

        for(Instance instance : allInstances){
            if(instance.getState()== StateAware.State.RUNNING)
                runningInstances.add(instance);
        }
        return runningInstances;
    }

    private void setNodesToCreate(int val){
        if(val<0)
            throw new IllegalArgumentException("Nodes must be >=0");
        nodesToCreate = val;
    }


    /* ************** */
    /* Action Classes */
    /* ************** */

    private class ComparatorAction implements CallableSeveralTimes<Boolean> {

        private int iterations;
        private int counter;
        private final int aspectedNumNodes;

        public ComparatorAction(int _aspectedNumNodes){
            aspectedNumNodes = _aspectedNumNodes;
        }

        @Override
        public Boolean call() throws Exception{
            counter++;
            log.info("comparing " + counter);

            if(runningInstances().size() != aspectedNumNodes){
                return false;           // c'è bisogno di fare ulteriori comparazioni
            } else {
                return true;            // non c'è bisogno di fare ulteriori comparazioni
            }
        }


        @Override
        public void setIterations(int val) {
            iterations = val;
        }
    }


    private class RunningCheckerAction implements CallableSeveralTimes<Boolean> {

        private int iterations;
        private int counter;
        private final String instanceId;

        public RunningCheckerAction(String _instanceId){
            instanceId = _instanceId;
        }

        @Override
        public Boolean call() throws Exception{
            counter++;
            log.info("checking running " + counter);

            Instance instance = client.listInstances(instanceId);
            if( !instance.isRunning() ){
                return false;    // c'è bisogno di fare ulteriori comparazioni
            } else {
                return true; // non c'è bisogno di fare ulteriori comparazioni
            }
        }

        @Override
        public void setIterations(int val) {
            iterations = val;
        }
    }

    private class CreatorAction implements CallableSeveralTimes<Boolean> {

        private int counter;

        @Override
        public Boolean call() throws Exception{
            log.info("creating");
            String instanceName = prefix.concat( String.valueOf( runningInstances().size() ) );
            Instance newInstance;
            try {
                newInstance = client.createInstance(instanceName, imageId, flavorId, null, null, null);
            } catch (DeltaCloudClientException e) {
                throw new RuntimeException(e);
            }

            ExecutorService actionExecutor = Executors.newSingleThreadExecutor();
            CountDownLatch latch = new CountDownLatch(1);
            FixedExecutionRunnable<CallableSeveralTimes<Boolean>> callableSeveralTimesChecker = new FixedExecutionRunnable(
                    new RunningCheckerAction(newInstance.getId()), 5, 5, TimeUnit.SECONDS, latch
            );

            actionExecutor.submit(callableSeveralTimesChecker);
            actionExecutor.shutdown();
            latch.await();

            if(callableSeveralTimesChecker.get()){
                return false; // passo a creare la prox macchina
            } else {
                throw new DeltaCloudException("Problem starting instace with id:" + newInstance.getId());
            }
        }

        @Override
        public void setIterations(int val) {
            counter = val;
        }
    }


    private class DestroyerAction implements CallableSeveralTimes<Boolean> {

        private int counter;

        @Override
        public Boolean call() throws Exception{
            log.info("destroying");

            List<Instance> instances = runningInstances();
            Instance instance = instances.get(0);

            try {
                SlaveKillerActuator slaveKillerActuator = SlaveKillerActuator.getInstance(instance.getPrivateAddresses().get(0),9998, "TpccBenchmark");

                slaveKillerActuator.actuate();
                Thread.sleep(10000);
                instance.stop(client);
            } catch (DeltaCloudClientException e) {
                throw new RuntimeException(e);
            }

            ExecutorService actionExecutor = Executors.newSingleThreadExecutor();
            CountDownLatch latch = new CountDownLatch(1);
            FixedExecutionRunnable<CallableSeveralTimes<Boolean>> runnable = new FixedExecutionRunnable(
                    new ComparatorAction(instances.size()-1), 5, 5, TimeUnit.SECONDS, latch
            );

            actionExecutor.submit( runnable );
            actionExecutor.shutdown();
            latch.await();

            if(runnable.get()){
                return false; // passo a eliminare eventuali prox macchine
            } else {
                throw new DeltaCloudException("Problem deleting instace with id:" + instance.getId());
            }
        }

        @Override
        public void setIterations(int val) {
            counter = val;
        }
    }

}