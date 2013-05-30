package eu.cloudtm.model;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/27/13
 */
public class Tuning {

    public enum TuningType{
        AUTO("auto"),MANUAL("manual");

        private final String text;

        private TuningType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

    }

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

    public TuningType type;
    public TuningMethod method;

    public Tuning(){
        type = TuningType.AUTO;
        method = TuningMethod.ANALYTICAL;
    }


}