package eu.cloudtm.commons;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public enum ReplicationProtocol {

    TWOPC("2PC"),TO("TO"),PB("PB");

    private final String wpmValue;

    private ReplicationProtocol(final String text) {
        this.wpmValue = text;
    }

    public static ReplicationProtocol getByWPMValue(String wpmValue){
        for ( ReplicationProtocol rp : values() ){
            if(rp.getWpmValue().equals(wpmValue))
                return rp;
        }
        throw new RuntimeException("No protocol available with wpmValue=" + wpmValue );
    }

    public String getWpmValue() {
        return wpmValue;
    }

}
