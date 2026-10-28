package ru.itmo.soa.shop.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import ru.itmo.soa.shop.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException exception) {
        return error(exception.getStatus(), exception.getMessage(), HttpHeaders.EMPTY);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnavailable(ResourceAccessException exception) {
        LOG.error("Vehicles service is unavailable", exception);
        return error(503, "Сервис коллекции недоступен", HttpHeaders.EMPTY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception exception) {
        if (exception instanceof org.springframework.web.ErrorResponse springError) {
            int status = springError.getStatusCode().value();
            return error(status, defaultMessage(status), springError.getHeaders());
        }
        LOG.error("Unhandled exception", exception);
        return error(500, "Внутренняя ошибка сервера", HttpHeaders.EMPTY);
    }

    private static ResponseEntity<ErrorResponse> error(int status, String message, HttpHeaders headers) {
        return ResponseEntity.status(status)
                .headers(headers)
                .contentType(MediaType.APPLICATION_XML)
                .body(new ErrorResponse(status, message));
    }

    private static String defaultMessage(int status) {
        return switch (status) {
            case 400 -> "Некорректный запрос";
            case 404 -> "Ресурс не найден";
            case 405 -> "Метод не поддерживается для данного ресурса";
            case 406 -> "Ответ может быть сформирован только в формате application/xml";
            case 415 -> "Неподдерживаемый Content-Type";
            default -> HttpStatus.resolve(status) != null
                    ? HttpStatus.resolve(status).getReasonPhrase()
                    : "Ошибка обработки запроса";
        };
    }
}
