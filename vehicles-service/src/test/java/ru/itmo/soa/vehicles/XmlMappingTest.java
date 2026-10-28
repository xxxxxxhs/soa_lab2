package ru.itmo.soa.vehicles;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.junit.jupiter.api.Test;
import ru.itmo.soa.vehicles.dto.CountResult;
import ru.itmo.soa.vehicles.dto.ErrorResponse;
import ru.itmo.soa.vehicles.dto.VehicleInput;
import ru.itmo.soa.vehicles.dto.VehicleList;
import ru.itmo.soa.vehicles.dto.VehiclePage;
import ru.itmo.soa.vehicles.dto.VehiclePatch;
import ru.itmo.soa.vehicles.model.Coordinates;
import ru.itmo.soa.vehicles.model.Vehicle;
import ru.itmo.soa.vehicles.model.VehicleType;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlMappingTest {

    private static final OffsetDateTime TIME = OffsetDateTime.of(2026, 10, 8, 15, 36, 0, 0, ZoneOffset.ofHours(3));

    private static final String VEHICLE_XML = "<vehicle><id>1</id><name>Aurora</name>"
            + "<coordinates><x>10</x><y>20</y></coordinates>"
            + "<creationDate>2026-10-08T15:36:00+03:00</creationDate>"
            + "<enginePower>150</enginePower><capacity>4.5</capacity>"
            + "<distanceTravelled>1500.5</distanceTravelled><type>BOAT</type>"
            + "<numberOfWheels>4</numberOfWheels></vehicle>";

    private static Vehicle sampleVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1);
        vehicle.setName("Aurora");
        vehicle.setCoordinates(new Coordinates(10, 20L));
        vehicle.setCreationDate(TIME);
        vehicle.setEnginePower(150L);
        vehicle.setCapacity(4.5);
        vehicle.setDistanceTravelled(1500.5f);
        vehicle.setType(VehicleType.BOAT);
        vehicle.setNumberOfWheels(4);
        return vehicle;
    }

    private static String marshal(Object value) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(value.getClass()).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(value, writer);
        return writer.toString();
    }

    private static <T> T unmarshal(String xml, Class<T> type) throws JAXBException {
        return type.cast(JAXBContext.newInstance(type).createUnmarshaller().unmarshal(new StringReader(xml)));
    }

    @Test
    void vehicle() throws JAXBException {
        assertEquals(VEHICLE_XML, marshal(sampleVehicle()));
    }

    @Test
    void vehiclePage() throws JAXBException {
        VehiclePage page = new VehiclePage(List.of(sampleVehicle()), 1, 20, 1, 1);
        assertEquals("<vehiclePage><vehicles>" + VEHICLE_XML + "</vehicles>"
                + "<page>1</page><size>20</size><totalElements>1</totalElements><totalPages>1</totalPages>"
                + "</vehiclePage>", marshal(page));
    }

    @Test
    void vehicleList() throws JAXBException {
        assertEquals("<vehicles>" + VEHICLE_XML + "</vehicles>", marshal(new VehicleList(List.of(sampleVehicle()))));
    }

    @Test
    void countResult() throws JAXBException {
        assertEquals("<countResult><count>3</count></countResult>", marshal(new CountResult(3)));
    }

    @Test
    void error() throws JAXBException {
        assertEquals("<error><code>404</code><message>Транспортное средство не найдено</message>"
                        + "<time>2026-10-08T15:36:00+03:00</time></error>",
                marshal(new ErrorResponse(404, "Транспортное средство не найдено", TIME)));
    }

    @Test
    void inputAppliesDefaults() throws JAXBException {
        VehicleInput input = unmarshal("<vehicle><name>Aurora</name><coordinates><y>20</y></coordinates>"
                + "<enginePower>150</enginePower><capacity>4.5</capacity>"
                + "<distanceTravelled>1500.5</distanceTravelled><type>BOAT</type></vehicle>", VehicleInput.class);
        assertEquals("Aurora", input.getName());
        assertEquals(0, input.getCoordinates().getX());
        assertEquals(20L, input.getCoordinates().getY());
        assertEquals(VehicleType.BOAT, input.getType());
        assertEquals(0, input.getNumberOfWheels());
    }

    @Test
    void patchKeepsAbsentFieldsNull() throws JAXBException {
        VehiclePatch patch = unmarshal("<vehicle><name>Aurora II</name><enginePower>200</enginePower></vehicle>",
                VehiclePatch.class);
        assertEquals("Aurora II", patch.getName());
        assertEquals(200L, patch.getEnginePower());
        assertNull(patch.getCoordinates());
        assertNull(patch.getNumberOfWheels());
        assertFalse(patch.isEmpty());
        assertTrue(unmarshal("<vehicle/>", VehiclePatch.class).isEmpty());
    }
}
