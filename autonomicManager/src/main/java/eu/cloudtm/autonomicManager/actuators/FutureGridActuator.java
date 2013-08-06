package eu.cloudtm.autonomicManager.actuators;

import com.jcraft.jsch.*;
import eu.cloudtm.InfinispanClient.InfinispanMachine;
import eu.cloudtm.autonomicManager.IActuator;
import eu.cloudtm.autonomicManager.actuators.excepions.ActuatorException;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import org.apache.deltacloud.client.Instance;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Author: Fabio Perfetti (perfabio87 [at] gmail.com)
 * Date: 8/6/13
 * Time: 10:54 AM
 */
public class FutureGridActuator implements IActuator {

    private JSch jsch=new JSch();

    private final String user = "root";

    private final int sshPort = 22;

    private final String privateKey = "~/.ssh/id_rsa";

    private Session session;

    private Set<String> machines = Collections.unmodifiableSet( new HashSet<String>(){{
        add("149.165.148.113"); add("149.165.148.242"); add("149.165.148.243"); add("149.165.148.244");
        add("149.165.148.245"); add("149.165.148.246"); add("149.165.148.247"); add("149.165.148.248");
        add("149.165.149.11");  add("149.165.149.12");  add("149.165.149.13");  add("149.165.149.100");
        add("149.165.149.101"); add("149.165.149.102"); add("149.165.149.103"); add("149.165.149.104");
        add("149.165.149.105"); add("149.165.149.106"); add("149.165.149.107"); add("149.165.149.108");
        add("149.165.149.109"); add("149.165.149.110"); add("149.165.149.111"); add("149.165.149.112");
        add("149.165.149.113"); add("149.165.149.114"); add("149.165.149.115"); add("149.165.149.116");
        add("149.165.149.118"); add("149.165.149.119"); add("149.165.149.120"); add("149.165.149.121");
        add("149.165.149.122"); add("149.165.149.123"); add("149.165.149.124"); add("149.165.149.125");
        add("149.165.149.126"); add("149.165.149.127"); add("149.165.149.128"); add("149.165.149.129");
    }});

    private Set<String> runningMachines = new HashSet<String>();



    private IActuator actuator;

    public FutureGridActuator(IActuator actuator){
        this.actuator = actuator;
    }

    @Override
    public void stopInstance() throws ActuatorException {

    }

    @Override
    public void startInstance() throws ActuatorException {

    }

    @Override
    public List<String> runningInstances() {
        return new ArrayList<String>(runningMachines);
    }

    @Override
    public void switchProtocol(ReplicationProtocol repProtocol) throws ActuatorException {
        actuator.switchProtocol(repProtocol);
    }

    @Override
    public void switchDegree(int degree) throws ActuatorException {
        actuator.switchDegree(degree);
    }

    private Session createSession() throws JSchException {


        jsch.addIdentity(privateKey);
        System.out.println("identity added ");
        session = jsch.getSession(user, machines.iterator().next(), sshPort);
        System.out.println("session created.");

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
        System.out.println("session connected.....");

        return session;
    }


    private void executeCommand(Session session, String command) throws JSchException, IOException {
        Channel channel= null;
        channel = session.openChannel("exec");
        ((ChannelExec)channel).setCommand(command);

        // X Forwarding
        // channel.setXForwarding(true);

        //channel.setInputStream(System.in);
        channel.setInputStream(null);

        //channel.setOutputStream(System.out);

        //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
        //((ChannelExec)channel).setErrStream(fos);
        ((ChannelExec)channel).setErrStream(System.err);

        InputStream in = null;
        in = channel.getInputStream();

        channel.connect();

        byte[] tmp=new byte[1024];
        while(true){
            while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                System.out.print(new String(tmp, 0, i));
            }
            if(channel.isClosed()){
                System.out.println("exit-status: "+channel.getExitStatus());
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

}
