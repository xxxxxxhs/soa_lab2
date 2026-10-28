package ru.itmo.soa.shop.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "vehicle")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"distanceTravelled", "numberOfWheels"})
public class VehiclePatch {

    private Float distanceTravelled;

    private Integer numberOfWheels;

    public static VehiclePatch distanceTravelled(float distanceTravelled) {
        VehiclePatch patch = new VehiclePatch();
        patch.distanceTravelled = distanceTravelled;
        return patch;
    }

    public static VehiclePatch numberOfWheels(int numberOfWheels) {
        VehiclePatch patch = new VehiclePatch();
        patch.numberOfWheels = numberOfWheels;
        return patch;
    }

    public Float getDistanceTravelled() {
        return distanceTravelled;
    }

    public Integer getNumberOfWheels() {
        return numberOfWheels;
    }
}
