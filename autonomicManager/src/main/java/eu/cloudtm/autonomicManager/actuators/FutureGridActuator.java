package eu.cloudtm.autonomicManager.actuators;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import eu.cloudtm.InfinispanClient.InfinispanClient;
import eu.cloudtm.InfinispanClient.InfinispanClientImpl;
import eu.cloudtm.InfinispanClient.InfinispanMachine;
import eu.cloudtm.InfinispanClient.exception.InvocationException;
import eu.cloudtm.InfinispanClient.exception.NoJmxProtocolRegisterException;
import eu.cloudtm.autonomicManager.Actuator;
import eu.cloudtm.autonomicManager.ControllerLogger;
import eu.cloudtm.autonomicManager.actuators.clients.ApplicationClient;
import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Fabio Perfetti (perfabio87 [at] gmail.com) Date: 8/6/13 Time: 10:54 AM
 */
public class FutureGridActuator implements Actuator {

   private static Log log = LogFactory.getLog(FutureGridActuator.class);

   private JSch jsch = new JSch();

   private final String user = Config.getInstance().getFutureGridUsername();

   private final int sshPort = 22;

   private final String privateKey = "~/.ssh/id_rsa";

   private Session session;

   /* APPLICATION CLIENT */
   private final ApplicationClient applicationClient;

    /* FUTUREGRID */

   private final Set<String> availableMachines = Collections.synchronizedSet(new HashSet<String>());

   private Set<String> runningMachines = Collections.synchronizedSet(new HashSet<String>());

   protected final int jmxPort;


   /* INFINISPAN CLIENT */
   private final String ispnDomain;

   private final String ispnCacheName;


   @Deprecated
   public FutureGridActuator(int jmxPort,
                             String ispnDomain,
                             String ispnCacheName,
                             Set<String> availableMachines,
                             Set<String> initUpMachines) {
      this.applicationClient = null;
      this.jmxPort = jmxPort;
      this.ispnDomain = ispnDomain;
      this.ispnCacheName = ispnCacheName;
      registerInitialMachineIfNeeded(availableMachines, initUpMachines);
      this.availableMachines.addAll(availableMachines);
      log.info("AvailableMachines: " + this.availableMachines.size());

   }

   public FutureGridActuator(ApplicationClient applicationClient,
                             int jmxPort,
                             String ispnDomain,
                             String ispnCacheName,
                             Set<String> availableMachines,
                             Set<String> initUpMachines) {
      this.applicationClient = applicationClient;
      this.jmxPort = jmxPort;
      this.ispnDomain = ispnDomain;
      this.ispnCacheName = ispnCacheName;
      registerInitialMachineIfNeeded(availableMachines, initUpMachines);
      this.availableMachines.addAll(availableMachines);
      log.info("AvailableMachines: " + this.availableMachines.size());

   }


   @Override
   public void stopApplication(String machine) throws ActuatorException {
      if( applicationClient!=null ){
         ControllerLogger.log.info(" * Stopping application on machine " + machine);
         applicationClient.stop(machine, jmxPort);
      }
   }


   //TODO: add boundary checks?
   private void registerInitialMachineIfNeeded(Set<String> availableMachines, Set<String> initUpMachines) {
      boolean needed = !(initUpMachines == null || initUpMachines.isEmpty());
      final boolean info = log.isInfoEnabled();
      if (needed) {
         if (info) {
            log.info("Available Machines  are " + availableMachines.size() + " initial are " + initUpMachines.size());
         }
         for (String s : initUpMachines) {
            if (info) {
               log.info("Removing " + s + " from available machines ");    //TODO check if it is present
            }
            availableMachines.remove(s);
            runningMachines.add(s);
         }
      }
      if (info) {
         log.info("After having registered nodes already up, we have " + availableMachines.size() + " available machines");
      }
   }

   @Override
   public synchronized void stopInstance() throws ActuatorException {

      final String command = Config.getInstance().getString(KeyConfig.FUTUREGRID_STOP_SCRIPT.key());
      final String machine = runningMachines.iterator().next();


      stopApplication(machine);

      try {
         Session session = createSession(machine);
         executeCommand(session, command);

         if (runningMachines.remove(machine)) {
            availableMachines.add(machine);
         }

         log.info("VM " + machine + " stopped!");

      } catch (JSchException e) {
         throw new ActuatorException(e);
      } catch (IOException e) {
         throw new ActuatorException(e);
      }

      log.info("available: " + availableMachines.size());
   }


   @Override
   public synchronized void startInstance() throws ActuatorException {

      final String command = Config.getInstance().getString(KeyConfig.FUTUREGRID_START_SCRIPT.key());
      final String machine = availableMachines.iterator().next();

      try {
         Session session = createSession(machine);
         executeCommand(session, command);

         if (availableMachines.remove(machine)) {
            runningMachines.add(machine);
         }
         log.info("VM " + machine + " started!");


      } catch (JSchException e) {
         throw new ActuatorException(e);
      } catch (IOException e) {
         throw new ActuatorException(e);
      } finally {
         session.disconnect();
      }

      log.info("available: " + availableMachines.size());
   }

   @Override
   public synchronized List<String> runningInstances() {
      return new ArrayList<String>(runningMachines);
   }

   @Override
   public void switchProtocol(ReplicationProtocol repProtocol) throws ActuatorException {

      Set<InfinispanMachine> ispnMachines = null;
      try {
         ispnMachines = instacesToIspnMachines();
      } catch (UnknownHostException e) {
         throw new ActuatorException(e);
      }

      if (ispnMachines.size() == 0) {
         log.info("No instances. Skipping...");
         return;
      }

      InfinispanClient infinispanClient = new InfinispanClientImpl(ispnMachines, ispnDomain, ispnCacheName);

      try {
         infinispanClient.triggerBlockingSwitchReplicationProtocol(repProtocol.getWpmValue(), false, false);

      } catch (InvocationException e) {
         throw new ActuatorException(e);
      } catch (NoJmxProtocolRegisterException e) {
         throw new ActuatorException(e);
      }
   }

   @Override
   public void switchDegree(int degree) throws ActuatorException {
      Set<InfinispanMachine> ispnMachines = null;
      try {
         ispnMachines = instacesToIspnMachines();
      } catch (UnknownHostException e) {
         throw new ActuatorException(e);
      }

      if (ispnMachines.size() == 0) {
         log.info("No instances. Skipping...");
         return;
      }

      InfinispanClient infinispanClient = new InfinispanClientImpl(ispnMachines, ispnDomain, ispnCacheName);

      try {
         infinispanClient.triggerBlockingSwitchReplicationDegree(degree);

      } catch (InvocationException e) {
         ControllerLogger.log.warn(e);
         throw new ActuatorException(e);
      } catch (NoJmxProtocolRegisterException e) {
         ControllerLogger.log.warn(e);
         throw new ActuatorException(e);
      }
   }

   @Override
   public void triggerRebalancing(boolean enabled) throws ActuatorException {
      Set<InfinispanMachine> ispnMachines = null;
      try {
         ispnMachines = instacesToIspnMachines();
      } catch (UnknownHostException e) {
         throw new ActuatorException(e);
      }

      if (ispnMachines.size() == 0) {
         log.info("No instances. Skipping...");
         return;
      }

      InfinispanClient infinispanClient = new InfinispanClientImpl(ispnMachines, ispnDomain, ispnCacheName);

      try {
         infinispanClient.triggerRebalancing(enabled);

      } catch (InvocationException e) {
         ControllerLogger.log.warn(e);
         throw new ActuatorException(e);
      } catch (NoJmxProtocolRegisterException e) {
         ControllerLogger.log.warn(e);
         throw new ActuatorException(e);
      }
   }

   private Session createSession(String host) throws JSchException {

      jsch.addIdentity(privateKey);
      log.trace("identity added ");
      session = jsch.getSession(user, host, sshPort);
      log.trace("session created.");

      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);

      session.connect();
      log.trace("session connected.....");

      return session;
   }


   private void executeCommand(Session session, String command) throws JSchException, IOException {
      Channel channel = null;
      channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand(command);

      // X Forwarding
      // channel.setXForwarding(true);

      //channel.setInputStream(System.in);
      channel.setInputStream(null);

      //channel.setOutputStream(System.out);

      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
      //((ChannelExec)channel).setErrStream(fos);
      ((ChannelExec) channel).setErrStream(System.err);

      InputStream in = null;
      in = channel.getInputStream();

      channel.connect();

      byte[] tmp = new byte[1024];
      while (true) {
         while (in.available() > 0) {
            int i = in.read(tmp, 0, 1024);
            if (i < 0) break;
            log.trace(new String(tmp, 0, i));
         }
         if (channel.isClosed()) {
            log.trace("exit-status: " + channel.getExitStatus());
            break;
         }
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      channel.disconnect();
   }

   private Set<InfinispanMachine> instacesToIspnMachines() throws UnknownHostException {
      Set<InfinispanMachine> ispnMachines = new HashSet<InfinispanMachine>();
      for (String machine : runningInstances()) {
         log.info("InetAddress for " + machine + " " + InetAddress.getByName(machine));
         String hostname = this.getHostName(machine);
         String address = machine;
         log.info("Mapping FutureGrid instances to infinispan machines (" + hostname + ", " + address + ")");

         ispnMachines.add(new InfinispanMachine(hostname, jmxPort, address));
      }
      return ispnMachines;
   }


   /*
      This can be of course optimized to avoid reading the config every and each time, and also the file. But for now...
    */

   private String getHostName(String machine) throws UnknownHostException {
      Configuration configuration = Config.getInstance();
      boolean readFromFile = configuration.containsKey(KeyConfig.FILE_HOSTNAME.key());
      boolean trace = log.isTraceEnabled();
      String hostName;
      if (readFromFile) {
         String file = configuration.getString(KeyConfig.FILE_HOSTNAME.key());
         hostName = readHostname(file, machine);
         if (trace) log.trace("Resolving hostname for machines " + machine + " to " + hostName + " using file " + file);
      } else {
         hostName = InetAddress.getByName(machine).getHostName();
         if (trace) log.trace("Resolving hostname for machines " + machine + " to " + hostName + " using inetAddress");
      }
      return hostName;
   }

   private String readHostname(String file, String machine) throws UnknownHostException {

      try {
         BufferedReader br = new BufferedReader(new FileReader(new File(file)));
         String line;
         while ((line = br.readLine()) != null) {
            if (line.contains(machine))
               return line.split(":")[1];
         }
      } catch (Exception e) {
         throw new UnknownHostException(e.getMessage());
      }
      throw new UnknownHostException("It was impossible to resolve the hostname for " + machine + ". Check " + file);
   }

}
