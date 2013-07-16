package eu.cloudtm.commons;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class PlatformTuning {

    private static Log log = LogFactory.getLog(PlatformTuning.class);

    private Forecaster forecaster = Forecaster.ANALYTICAL;

    private boolean autoScale = true;
    private boolean autoDegree = true;
    private boolean autoProtocol = true;

    public PlatformTuning(Forecaster forecaster, boolean defaultTuning){
        this.forecaster = forecaster;
        autoScale = autoDegree = autoProtocol = defaultTuning;
    }

    public Forecaster forecaster(){
        return forecaster;
    }

    public boolean isAutoScale(){
        return autoScale;
    }

    public boolean isAutoDegree(){
        return autoDegree;
    }

    public boolean isAutoProtocol(){
        return autoProtocol;
    }

    public PlatformTuning toJSON() {
        log.info("TO IMPLEMENT");
        Gson gson = new Gson();
        PlatformTuning state = gson.fromJson(gson.toJson(this), PlatformTuning.class);
        return state;
    }
}
