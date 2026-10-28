package ru.itmo.soa.vehicles.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");

    @Override
    public OffsetDateTime unmarshal(String value) {
        return OffsetDateTime.parse(value);
    }

    @Override
    public String marshal(OffsetDateTime value) {
        return value.atZoneSameInstant(ZoneId.systemDefault()).format(FORMAT);
    }
}
