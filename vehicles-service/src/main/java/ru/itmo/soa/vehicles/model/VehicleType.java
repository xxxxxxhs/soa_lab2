package ru.itmo.soa.vehicles.model;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum VehicleType {
    PLANE,
    HELICOPTER,
    BOAT,
    SHIP,
    BICYCLE
}
