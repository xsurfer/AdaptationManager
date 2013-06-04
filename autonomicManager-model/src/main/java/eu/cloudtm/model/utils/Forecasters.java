package eu.cloudtm.model.utils;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/30/13
 */
public enum Forecasters {
    NONE("none"),ANALYTICAL("analytical"),SIMULATOR("simulator"),MACHINELEARNING("machine learning");

    private final String text;

    private Forecasters(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
