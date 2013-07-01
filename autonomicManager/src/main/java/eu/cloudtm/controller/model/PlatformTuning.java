package eu.cloudtm.controller.model;

import com.google.gson.Gson;
import eu.cloudtm.controller.model.utils.InstanceConfig;
import eu.cloudtm.controller.model.utils.ReplicationProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class PlatformTuning {

    private static Log log = LogFactory.getLog(PlatformTuning.class);

    private Tuning scaleTuning = new Tuning();

    private Tuning repProtocolTuning = new Tuning();

    private Tuning repDegreeTuning = new Tuning();

    private Boolean dataPlacement = true;


    public PlatformTuning toJSON() {
        log.info("TO IMPLEMENT");
        Gson gson = new Gson();
        PlatformTuning state = gson.fromJson(gson.toJson(this), PlatformTuning.class);
        return state;
    }
}
