package ru.itmo.soa.vehicles.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ru.itmo.soa.vehicles.dto.ErrorResponse;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response original = exception.getResponse();
        int status = original.getStatus();
        String message = exception instanceof ApiException ? exception.getMessage() : defaultMessage(status);

        Response.ResponseBuilder builder = Response.status(status)
                .type(MediaType.APPLICATION_XML_TYPE)
                .entity(new ErrorResponse(status, message));
        if (original.getHeaderString(HttpHeaders.ALLOW) != null) {
            builder.header(HttpHeaders.ALLOW, original.getHeaderString(HttpHeaders.ALLOW));
        }
        return builder.build();
    }

    private static String defaultMessage(int status) {
        return switch (status) {
            case 400 -> "Некорректный запрос";
            case 404 -> "Ресурс не найден";
            case 405 -> "Метод не поддерживается для данного ресурса";
            case 406 -> "Ответ может быть сформирован только в формате application/xml";
            case 415 -> "Поддерживается только Content-Type application/xml";
            default -> Response.Status.fromStatusCode(status) != null
                    ? Response.Status.fromStatusCode(status).getReasonPhrase()
                    : "Ошибка обработки запроса";
        };
    }
}
