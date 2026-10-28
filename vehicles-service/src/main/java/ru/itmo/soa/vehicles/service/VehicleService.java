package ru.itmo.soa.vehicles.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import ru.itmo.soa.vehicles.dto.VehicleInput;
import ru.itmo.soa.vehicles.dto.VehiclePage;
import ru.itmo.soa.vehicles.dto.VehiclePatch;
import ru.itmo.soa.vehicles.error.ApiException;
import ru.itmo.soa.vehicles.model.Coordinates;
import ru.itmo.soa.vehicles.model.Vehicle;
import ru.itmo.soa.vehicles.model.VehicleType;
import ru.itmo.soa.vehicles.query.FilterCondition;
import ru.itmo.soa.vehicles.query.SortOrder;
import ru.itmo.soa.vehicles.query.VehicleField;
import ru.itmo.soa.vehicles.query.VehicleQuery;
import ru.itmo.soa.vehicles.validation.RequestValidator;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class VehicleService {

    private static final String NOT_FOUND = "Транспортное средство не найдено";

    @PersistenceContext(unitName = "vehicles")
    EntityManager em;

    @Inject
    RequestValidator validator;

    public Vehicle create(VehicleInput input) {
        validator.validate(input);
        Vehicle vehicle = new Vehicle();
        apply(vehicle, input);
        vehicle.setCreationDate(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        em.persist(vehicle);
        return vehicle;
    }

    public Vehicle get(int id) {
        Vehicle vehicle = id > 0 ? em.find(Vehicle.class, id) : null;
        if (vehicle == null) {
            throw ApiException.notFound(NOT_FOUND);
        }
        return vehicle;
    }

    public Vehicle replace(int id, VehicleInput input) {
        validator.validate(input);
        Vehicle vehicle = get(id);
        apply(vehicle, input);
        return vehicle;
    }

    public Vehicle patch(int id, VehiclePatch patch) {
        if (patch.isEmpty()) {
            throw ApiException.unprocessable("Тело запроса должно содержать хотя бы одно поле");
        }
        validator.validate(patch);
        Vehicle vehicle = get(id);
        if (patch.getName() != null) {
            vehicle.setName(patch.getName());
        }
        if (patch.getCoordinates() != null) {
            vehicle.setCoordinates(copy(patch.getCoordinates()));
        }
        if (patch.getEnginePower() != null) {
            vehicle.setEnginePower(patch.getEnginePower());
        }
        if (patch.getCapacity() != null) {
            vehicle.setCapacity(patch.getCapacity());
        }
        if (patch.getDistanceTravelled() != null) {
            vehicle.setDistanceTravelled(patch.getDistanceTravelled());
        }
        if (patch.getType() != null) {
            vehicle.setType(patch.getType());
        }
        if (patch.getNumberOfWheels() != null) {
            vehicle.setNumberOfWheels(patch.getNumberOfWheels());
        }
        return vehicle;
    }

    public void delete(int id) {
        em.remove(get(id));
    }

    public Optional<VehiclePage> find(VehicleQuery query) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Vehicle> countRoot = countQuery.from(Vehicle.class);
        countQuery.select(cb.count(countRoot)).where(predicates(cb, countRoot, query.filters()));
        long total = em.createQuery(countQuery).getSingleResult();

        long offset = (long) (query.page() - 1) * query.size();
        if (offset >= total) {
            return Optional.empty();
        }

        CriteriaQuery<Vehicle> select = cb.createQuery(Vehicle.class);
        Root<Vehicle> root = select.from(Vehicle.class);
        select.select(root)
                .where(predicates(cb, root, query.filters()))
                .orderBy(orders(cb, root, query.sorts()));
        List<Vehicle> vehicles = em.createQuery(select)
                .setFirstResult((int) offset)
                .setMaxResults(query.size())
                .getResultList();

        int totalPages = (int) ((total + query.size() - 1) / query.size());
        return Optional.of(new VehiclePage(vehicles, query.page(), query.size(), total, totalPages));
    }

    public Optional<Vehicle> findWithMaxEnginePower() {
        return em.createQuery("select v from Vehicle v order by v.enginePower desc, v.id asc", Vehicle.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public long countWithDistanceTravelledGreaterThan(float distanceTravelled) {
        return em.createQuery("select count(v) from Vehicle v where v.distanceTravelled > :distance", Long.class)
                .setParameter("distance", distanceTravelled)
                .getSingleResult();
    }

    public List<Vehicle> findWithTypeLessThan(VehicleType type) {
        List<VehicleType> lesser = Arrays.stream(VehicleType.values())
                .filter(candidate -> candidate.ordinal() < type.ordinal())
                .toList();
        if (lesser.isEmpty()) {
            return List.of();
        }
        return em.createQuery("select v from Vehicle v where v.type in :types order by v.id", Vehicle.class)
                .setParameter("types", lesser)
                .getResultList();
    }

    private static void apply(Vehicle vehicle, VehicleInput input) {
        vehicle.setName(input.getName());
        vehicle.setCoordinates(copy(input.getCoordinates()));
        vehicle.setEnginePower(input.getEnginePower());
        vehicle.setCapacity(input.getCapacity());
        vehicle.setDistanceTravelled(input.getDistanceTravelled());
        vehicle.setType(input.getType());
        vehicle.setNumberOfWheels(input.getNumberOfWheels());
    }

    private static Coordinates copy(Coordinates coordinates) {
        return new Coordinates(coordinates.getX(), coordinates.getY());
    }

    private static Predicate[] predicates(CriteriaBuilder cb, Root<Vehicle> root, List<FilterCondition> filters) {
        return filters.stream()
                .map(filter -> predicate(cb, root, filter))
                .toArray(Predicate[]::new);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Predicate predicate(CriteriaBuilder cb, Root<Vehicle> root, FilterCondition filter) {
        Expression path = filter.field().path(root);
        Comparable value = filter.value();
        return switch (filter.operator()) {
            case EQ -> cb.equal(path, value);
            case NE -> cb.notEqual(path, value);
            case GT -> cb.greaterThan(path, value);
            case GTE -> cb.greaterThanOrEqualTo(path, value);
            case LT -> cb.lessThan(path, value);
            case LTE -> cb.lessThanOrEqualTo(path, value);
        };
    }

    private static List<Order> orders(CriteriaBuilder cb, Root<Vehicle> root, List<SortOrder> sorts) {
        List<Order> orders = new ArrayList<>();
        boolean sortedById = false;
        for (SortOrder sort : sorts) {
            Expression<?> expression = sort.field() == VehicleField.TYPE ? typeRank(cb, root) : sort.field().path(root);
            orders.add(sort.descending() ? cb.desc(expression) : cb.asc(expression));
            sortedById |= sort.field() == VehicleField.ID;
        }
        if (!sortedById) {
            orders.add(cb.asc(root.get("id")));
        }
        return orders;
    }

    private static Expression<Integer> typeRank(CriteriaBuilder cb, Root<Vehicle> root) {
        CriteriaBuilder.SimpleCase<VehicleType, Integer> rank = cb.selectCase(root.get("type"));
        for (VehicleType type : VehicleType.values()) {
            rank = rank.when(type, type.ordinal());
        }
        return rank.otherwise(VehicleType.values().length);
    }
}
