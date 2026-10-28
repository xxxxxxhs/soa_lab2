package ru.itmo.soa.vehicles.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.itmo.soa.vehicles.xml.OffsetDateTimeAdapter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "vehicles")
@XmlRootElement(name = "vehicle")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "id", "name", "coordinates", "creationDate", "enginePower",
        "capacity", "distanceTravelled", "type", "numberOfWheels"
})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "text collate \"C\"")
    private String name;

    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "coordinate_x", nullable = false))
    @AttributeOverride(name = "y", column = @Column(name = "coordinate_y", nullable = false))
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false)
    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    private OffsetDateTime creationDate;

    @Column(name = "engine_power", nullable = false)
    private Long enginePower;

    @Column(nullable = false)
    private Double capacity;

    @Column(name = "distance_travelled", nullable = false)
    private Float distanceTravelled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private VehicleType type;

    @Column(name = "number_of_wheels", nullable = false)
    private Integer numberOfWheels;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(Long enginePower) {
        this.enginePower = enginePower;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Float getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(Float distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public Integer getNumberOfWheels() {
        return numberOfWheels;
    }

    public void setNumberOfWheels(Integer numberOfWheels) {
        this.numberOfWheels = numberOfWheels;
    }
}
