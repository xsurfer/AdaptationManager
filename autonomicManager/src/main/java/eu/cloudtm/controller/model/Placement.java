package eu.cloudtm.controller.model;

import com.google.gson.Gson;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class Placement extends AbstractTuned implements JSONClonable<Placement> {

    public Placement(){
    }

    @Override
    public Placement cloneJSON() {
        Gson gson = new Gson();
        Placement placement = gson.fromJson(gson.toJson(this), Placement.class);
        return placement;
    }
}
