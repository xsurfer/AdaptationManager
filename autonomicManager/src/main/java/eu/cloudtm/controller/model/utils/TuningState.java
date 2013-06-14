package eu.cloudtm.controller.model.utils;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/30/13
 */
public enum TuningState {
    SELF(true),MANUAL(false);

    private boolean val;

    private TuningState(final boolean val) {
        this.val = val;
    }

    public boolean getValue() {
        return val;
    }
}