package eu.cloudtm.model;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/30/13
 */
public enum TuningMethod{
    NONE("none"),ANALYTICAL("analytical"),SIMULATOR("simulator"),MACHINELEARNING("machine learning");

    private final String text;

    private TuningMethod(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
