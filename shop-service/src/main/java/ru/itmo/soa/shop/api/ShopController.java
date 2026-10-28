package ru.itmo.soa.shop.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.soa.shop.service.ShopService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {

    private final ShopService shop;

    public ShopController(ShopService shop) {
        this.shop = shop;
    }

    @PatchMapping("/fix-distance/{vehicle-id}")
    public ResponseEntity<Void> fixDistance(@PathVariable("vehicle-id") String vehicleId) {
        return seeOther(shop.fixDistance(PathParams.parseVehicleId(vehicleId)));
    }

    @PatchMapping("/add-wheels/{vehicle-id}/{number-of-wheels}")
    public ResponseEntity<Void> addWheels(@PathVariable("vehicle-id") String vehicleId,
                                          @PathVariable("number-of-wheels") String numberOfWheels) {
        int id = PathParams.parseVehicleId(vehicleId);
        int wheels = PathParams.parseNumberOfWheels(numberOfWheels);
        return seeOther(shop.addWheels(id, wheels));
    }

    private static ResponseEntity<Void> seeOther(URI location) {
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();
    }
}
