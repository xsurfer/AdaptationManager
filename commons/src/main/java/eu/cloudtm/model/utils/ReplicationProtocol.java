package eu.cloudtm.model.utils;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public enum ReplicationProtocol {

    TWOPC("2pc"),TOM("tom"),PB("pb");

    private final String text;

    private ReplicationProtocol(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
