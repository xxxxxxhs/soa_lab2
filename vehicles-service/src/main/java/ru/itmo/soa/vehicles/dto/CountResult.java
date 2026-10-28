package ru.itmo.soa.vehicles.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "countResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountResult {

    private long count;

    public CountResult() {
    }

    public CountResult(long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }
}
