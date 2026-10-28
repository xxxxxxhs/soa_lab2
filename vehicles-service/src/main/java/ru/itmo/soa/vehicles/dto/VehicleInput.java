package ru.itmo.soa.vehicles.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.itmo.soa.vehicles.model.Coordinates;
import ru.itmo.soa.vehicles.model.VehicleType;
import ru.itmo.soa.vehicles.validation.Finite;
import ru.itmo.soa.vehicles.xml.VehicleTypeAdapter;

@XmlRootElement(name = "vehicle")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "name", "coordinates", "enginePower", "capacity",
        "distanceTravelled", "type", "numberOfWheels"
})
public class VehicleInput {

    @NotNull(message = "поле обязательно")
    @Size(min = 1, message = "строка не может быть пустой")
    private String name;

    @NotNull(message = "поле обязательно")
    @Valid
    private Coordinates coordinates;

    @NotNull(message = "поле обязательно")
    @Min(value = 1, message = "значение должно быть больше 0")
    private Long enginePower;

    @Finite
    @NotNull(message = "поле обязательно")
    @Positive(message = "значение должно быть больше 0")
    private Double capacity;

    @Finite
    @NotNull(message = "поле обязательно")
    @PositiveOrZero(message = "значение не может быть отрицательным")
    private Float distanceTravelled;

    @NotNull(message = "поле обязательно")
    @XmlJavaTypeAdapter(VehicleTypeAdapter.class)
    private VehicleType type;

    @NotNull(message = "поле обязательно")
    @PositiveOrZero(message = "значение не может быть отрицательным")
    private Integer numberOfWheels = 0;

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
