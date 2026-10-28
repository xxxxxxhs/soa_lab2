package ru.itmo.soa.shop.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.itmo.soa.shop.xml.OffsetDateTimeAdapter;

import java.time.OffsetDateTime;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"code", "message", "time"})
public class ErrorResponse {

    private int code;

    private String message;

    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    private OffsetDateTime time;

    public ErrorResponse() {
    }

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.time = OffsetDateTime.now();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public OffsetDateTime getTime() {
        return time;
    }
}
