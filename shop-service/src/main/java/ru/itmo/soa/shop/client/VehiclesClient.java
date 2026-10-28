package ru.itmo.soa.shop.client;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itmo.soa.shop.dto.ErrorResponse;
import ru.itmo.soa.shop.dto.Vehicle;
import ru.itmo.soa.shop.dto.VehiclePatch;
import ru.itmo.soa.shop.error.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class VehiclesClient {

    private static final JAXBContext ERROR_CONTEXT = createErrorContext();

    private final String baseUrl;

    private final RestClient restClient;

    public VehiclesClient(@Value("${vehicles.base-url}") String baseUrl, RestClient.Builder builder, SslBundles sslBundles) {
        this.baseUrl = baseUrl;
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslBundles.getBundle("vehicles").createSslContext())
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw toApiException(response);
                })
                .build();
    }

    public Vehicle get(int id) {
        return restClient.get()
                .uri("/vehicles/{id}", id)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .body(Vehicle.class);
    }

    public void patch(int id, VehiclePatch patch) {
        restClient.patch()
                .uri("/vehicles/{id}", id)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .body(patch)
                .retrieve()
                .toBodilessEntity();
    }

    public URI vehicleUri(int id) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path("/vehicles/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    private static ApiException toApiException(ClientHttpResponse response) throws IOException {
        int status = response.getStatusCode().value();
        if (status >= 500) {
            return new ApiException(502, "Сервис коллекции вернул ошибку");
        }
        return new ApiException(status, readMessage(response.getBody()));
    }

    private static String readMessage(InputStream body) {
        try {
            ErrorResponse error = (ErrorResponse) ERROR_CONTEXT.createUnmarshaller().unmarshal(body);
            return error.getMessage();
        } catch (JAXBException | ClassCastException e) {
            return "Сервис коллекции отклонил запрос";
        }
    }

    private static JAXBContext createErrorContext() {
        try {
            return JAXBContext.newInstance(ErrorResponse.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }
}
