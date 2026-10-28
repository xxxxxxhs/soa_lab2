package ru.itmo.soa.vehicles.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.vehicles.dto.ErrorResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(UnhandledExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        LOG.log(Level.SEVERE, "Unhandled exception", exception);
        return Response.serverError()
                .type(MediaType.APPLICATION_XML_TYPE)
                .entity(new ErrorResponse(500, "Внутренняя ошибка сервера"))
                .build();
    }
}
