package com.cogito.erm.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="vessels")
public class Vessels {

@Id
private String id;

private String vesselName;

public String getId() {
    return id;
}

public void setId(String id) {
    this.id = id;
}

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }
}
