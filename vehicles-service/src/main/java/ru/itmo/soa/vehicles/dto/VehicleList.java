package ru.itmo.soa.vehicles.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import ru.itmo.soa.vehicles.model.Vehicle;

import java.util.List;

@XmlRootElement(name = "vehicles")
@XmlAccessorType(XmlAccessType.FIELD)
public class VehicleList {

    @XmlElement(name = "vehicle")
    private List<Vehicle> vehicles;

    public VehicleList() {
    }

    public VehicleList(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }
}
