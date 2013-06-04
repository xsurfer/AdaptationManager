package eu.cloudtm.model.utils;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/27/13
 */
public enum InstanceConfigurations {
    NONE(""),SMALL("small"), MEDIUM("medium"), LARGE("large");
    
    private final String text;

    private InstanceConfigurations(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}