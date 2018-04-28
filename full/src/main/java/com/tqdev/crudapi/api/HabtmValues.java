package com.tqdev.crudapi.api;

import java.util.ArrayList;
import java.util.HashMap;

public class HabtmValues {

    protected HashMap<Object, ArrayList<Object>> pkValues;
    protected HashMap<Object, Object> fkValues;

    public HabtmValues(HashMap<Object, ArrayList<Object>> pkValues, HashMap<Object, Object> fkValues) {
        this.pkValues = pkValues;
        this.fkValues = fkValues;
    }

}
