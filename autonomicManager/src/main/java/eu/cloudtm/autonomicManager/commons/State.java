package eu.cloudtm.autonomicManager.commons;

/**
 * Thread-safe class
 *
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/24/13
 */
public class State {

    PlatformState state;

    public State(PlatformState initialState){
        this.state = initialState;
    }

    public synchronized void update(PlatformState nextState){
        this.state = nextState;
    }

    public synchronized PlatformState current(){
        return this.state;
    }

    public boolean isRunning(){
        return (state.equals(PlatformState.RUNNING)) ? true : false;
    }

    public boolean isError(){
        return (state.equals(PlatformState.ERROR)) ? true : false;
    }

    public boolean isReconfiguring(){
        return (state.equals(PlatformState.RECONFIGURING)) ? true : false;
    }

}

