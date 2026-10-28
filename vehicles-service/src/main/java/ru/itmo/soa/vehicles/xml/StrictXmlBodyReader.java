package ru.itmo.soa.vehicles.xml;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import ru.itmo.soa.vehicles.error.ApiException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
@Consumes(MediaType.APPLICATION_XML)
public class StrictXmlBodyReader implements MessageBodyReader<Object> {

    private static final String MALFORMED_BODY = "Тело запроса не является корректным XML";

    private final Map<Class<?>, JAXBContext> contexts = new ConcurrentHashMap<>();

    private final XMLInputFactory inputFactory;

    public StrictXmlBodyReader() {
        inputFactory = XMLInputFactory.newFactory();
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAnnotationPresent(XmlRootElement.class)
                && type.getPackageName().startsWith("ru.itmo.soa.vehicles");
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        try {
            Unmarshaller unmarshaller = context(type).createUnmarshaller();
            unmarshaller.setEventHandler(event -> false);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(entityStream);
            Object result = JAXBIntrospector.getValue(unmarshaller.unmarshal(reader));
            if (!type.isInstance(result)) {
                throw ApiException.badRequest(MALFORMED_BODY);
            }
            return result;
        } catch (JAXBException | XMLStreamException e) {
            throw ApiException.badRequest(MALFORMED_BODY);
        }
    }

    private JAXBContext context(Class<?> type) throws JAXBException {
        JAXBContext context = contexts.get(type);
        if (context == null) {
            context = JAXBContext.newInstance(type);
            contexts.put(type, context);
        }
        return context;
    }
}
