package ru.itmo.soa.vehicles.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import ru.itmo.soa.vehicles.dto.CountResult;
import ru.itmo.soa.vehicles.dto.VehicleInput;
import ru.itmo.soa.vehicles.dto.VehicleList;
import ru.itmo.soa.vehicles.dto.VehiclePatch;
import ru.itmo.soa.vehicles.model.Vehicle;
import ru.itmo.soa.vehicles.query.VehicleQueryParser;
import ru.itmo.soa.vehicles.service.VehicleService;

import java.net.URI;
import java.util.List;

@Path("/vehicles")
@Produces(MediaType.APPLICATION_XML)
public class VehicleResource {

    @Inject
    VehicleService service;

    @Context
    UriInfo uriInfo;

    @GET
    public Response list() {
        return service.find(VehicleQueryParser.parse(uriInfo.getQueryParameters()))
                .map(page -> Response.ok(page).build())
                .orElseGet(() -> Response.noContent().build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response create(VehicleInput input) {
        Vehicle vehicle = service.create(input);
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(vehicle.getId())).build();
        return Response.created(location).entity(vehicle).build();
    }

    @GET
    @Path("{id}")
    public Vehicle get(@PathParam("id") String id) {
        return service.get(PathParams.parseId(id));
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Vehicle replace(@PathParam("id") String id, VehicleInput input) {
        return service.replace(PathParams.parseId(id), input);
    }

    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Vehicle patch(@PathParam("id") String id, VehiclePatch patch) {
        return service.patch(PathParams.parseId(id), patch);
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        service.delete(PathParams.parseId(id));
        return Response.noContent().build();
    }

    @GET
    @Path("max-engine-power")
    public Response maxEnginePower() {
        return service.findWithMaxEnginePower()
                .map(vehicle -> Response.ok(vehicle).build())
                .orElseGet(() -> Response.noContent().build());
    }

    @GET
    @Path("distance-travelled/greater-than/{distanceTravelled}/count")
    public CountResult countByDistanceTravelled(@PathParam("distanceTravelled") String distanceTravelled) {
        return new CountResult(service.countWithDistanceTravelledGreaterThan(PathParams.parseDistance(distanceTravelled)));
    }

    @GET
    @Path("type/less-than/{type}")
    public Response typeLessThan(@PathParam("type") String type) {
        List<Vehicle> vehicles = service.findWithTypeLessThan(PathParams.parseType(type));
        if (vehicles.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(new VehicleList(vehicles)).build();
    }
}
