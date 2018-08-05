package com.tqdev.crudapi.record;

import java.util.ArrayList;
import java.util.HashMap;

public class HabtmValues {

    public HashMap<Object, ArrayList<Object>> pkValues;
    public HashMap<Object, Object> fkValues;

    public HabtmValues(HashMap<Object, ArrayList<Object>> pkValues, HashMap<Object, Object> fkValues) {
        this.pkValues = pkValues;
        this.fkValues = fkValues;
    }

}
