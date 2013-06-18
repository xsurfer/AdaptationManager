package eu.cloudtm.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class InputFilter {

    private static Log log = LogFactory.getLog(InputFilter.class);

    private static final int DELTA_ARRIVAL_RATE = 10;

    private static final int DELTA_ABORT_RATE = 10;

    private static final int DELTA_RESPONSE_TIME = 10;

    private Random rnd = new Random();

    public boolean doFilter(){
        // Calcola le medie e vede se c'è variazione


        //boolean reconfigure = rnd.nextBoolean();
        boolean reconfigure = false; // non riconfiguriamo
        return reconfigure;
    }

}

