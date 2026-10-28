package ru.itmo.soa.shop.service;

import org.springframework.stereotype.Service;
import ru.itmo.soa.shop.client.VehiclesClient;
import ru.itmo.soa.shop.dto.Vehicle;
import ru.itmo.soa.shop.dto.VehiclePatch;
import ru.itmo.soa.shop.error.ApiException;

import java.net.URI;

@Service
public class ShopService {

    private final VehiclesClient vehicles;

    public ShopService(VehiclesClient vehicles) {
        this.vehicles = vehicles;
    }

    public URI fixDistance(int vehicleId) {
        vehicles.patch(vehicleId, VehiclePatch.distanceTravelled(0));
        return vehicles.vehicleUri(vehicleId);
    }

    public URI addWheels(int vehicleId, int numberOfWheels) {
        Vehicle vehicle = vehicles.get(vehicleId);
        int total;
        try {
            total = Math.addExact(vehicle.getNumberOfWheels(), numberOfWheels);
        } catch (ArithmeticException e) {
            throw ApiException.unprocessable("number-of-wheels: итоговое количество колёс превышает допустимое значение");
        }
        vehicles.patch(vehicleId, VehiclePatch.numberOfWheels(total));
        return vehicles.vehicleUri(vehicleId);
    }
}
