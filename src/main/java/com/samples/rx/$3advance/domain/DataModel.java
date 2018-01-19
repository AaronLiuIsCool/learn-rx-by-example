package com.samples.rx.$3advance.domain;

import java.util.List;
import java.util.Map;

public abstract class DataModel {

    private Map<String, Object> metaData;
    private List<Errare> errors;

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public List<Errare> getErrors() {
        return errors;
    }

    public void setErrors(List<Errare> errors) {
        this.errors = errors;
    }
}
