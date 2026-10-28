package ru.itmo.soa.shop.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vehicle")
@XmlAccessorType(XmlAccessType.FIELD)
public class Vehicle {

    private Integer id;

    private Integer numberOfWheels;

    public Integer getId() {
        return id;
    }

    public Integer getNumberOfWheels() {
        return numberOfWheels;
    }
}
