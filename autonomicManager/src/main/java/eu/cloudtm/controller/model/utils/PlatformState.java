package eu.cloudtm.controller.model.utils;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public enum PlatformState {
    WORKING("working"),PENDING("pending"),ERROR("error");

    private final String text;

    private PlatformState(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
