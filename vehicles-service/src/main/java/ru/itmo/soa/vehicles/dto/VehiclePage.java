package ru.itmo.soa.vehicles.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import ru.itmo.soa.vehicles.model.Vehicle;

import java.util.List;

@XmlRootElement(name = "vehiclePage")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"vehicles", "page", "size", "totalElements", "totalPages"})
public class VehiclePage {

    @XmlElementWrapper(name = "vehicles")
    @XmlElement(name = "vehicle")
    private List<Vehicle> vehicles;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    public VehiclePage() {
    }

    public VehiclePage(List<Vehicle> vehicles, int page, int size, long totalElements, int totalPages) {
        this.vehicles = vehicles;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
