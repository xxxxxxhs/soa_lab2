package ru.itmo.soa.vehicles.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import ru.itmo.soa.vehicles.model.VehicleType;

public class VehicleTypeAdapter extends XmlAdapter<String, VehicleType> {

    @Override
    public VehicleType unmarshal(String value) {
        return VehicleType.valueOf(value.trim());
    }

    @Override
    public String marshal(VehicleType value) {
        return value.name();
    }
}
