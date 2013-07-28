package eu.cloudtm.autonomicManager.RESTServer;

import eu.cloudtm.autonomicManager.statistics.StatsManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 7/2/13
 */
public class Binder extends AbstractBinder {

    private StatsManager statsManager;

    public Binder(StatsManager statsManager){
        this.statsManager = statsManager;
    }

    @Override
    protected void configure() {
        //singleton binding
        bind(StatsManager.class).in(Singleton.class);
        bind(statsManager).to(StatsManager.class);
    }


}
