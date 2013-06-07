package eu.cloudtm.controller.model.utils;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/30/13
 */
public enum TuningType{
    SELF("self"),MANUAL("manual");

    private final String text;

    private TuningType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}