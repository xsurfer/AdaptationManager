package eu.cloudtm.controller.model.utils;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public enum ReplicationProtocols {

    TWOPC("2pc"),TOM("tom"),PB("pb");

    private final String text;

    private ReplicationProtocols(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
