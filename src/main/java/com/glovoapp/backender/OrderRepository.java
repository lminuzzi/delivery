package com.glovoapp.backender;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
class OrderRepository {
	private static final String ORDERS_FILE = "/orders.json";
	private static final List<Order> orders;

	static {
		try (Reader reader = new InputStreamReader(OrderRepository.class.getResourceAsStream(ORDERS_FILE))) {
			Type type = new TypeToken<List<Order>>() {
			}.getType();
			orders = new Gson().fromJson(reader, type);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	List<Order> findAll() {
		return new ArrayList<>(orders);
	}

	List<Order> findByCourier(Courier courier, String hideDescriptionWords, long hideDistance) {
		Stream<Order> streamOrders = orders.stream();
		if (!courier.getBox()) {
			List<String> descriptionWords = Arrays.asList(hideDescriptionWords.trim().toLowerCase().split(","));
			streamOrders = streamOrders.filter(order -> descriptionWords.parallelStream()
					.noneMatch(order.getDescription().toLowerCase()::contains));
		}
		if (!courier.getVehicle().equals(Vehicle.ELECTRIC_SCOOTER)
				&& !courier.getVehicle().equals(Vehicle.MOTORCYCLE)) {
			streamOrders = streamOrders.filter(order -> DistanceCalculator.calculateDistance(order.getPickup(),
					courier.getLocation()) <= hideDistance);
		}
		return streamOrders.collect(Collectors.toList());
	}

	public List<Order> orderByPriorities(List<Order> findByCourier, String priorities, long distanceSlot,
			Location distanceCourier) {
		List<Order> orderedOrders = new ArrayList<Order>();
		String[] splitPriorities = priorities.toLowerCase().trim().split(",");
		for (String priority : splitPriorities) {
			Supplier<Stream<Order>> streamSupplier = () -> findByCourier.stream();
			Stream<Order> firstOrders;
			switch (priority) {
			case "close":
				firstOrders = streamSupplier.get()
						.filter(order -> DistanceCalculator.calculateDistance(order.getPickup(), distanceCourier)
								* 1000 <= distanceSlot);
				Stream<Order> secondOrders = streamSupplier.get()
						.filter(order -> DistanceCalculator.calculateDistance(order.getPickup(), distanceCourier)
								* 1000 > distanceSlot
								&& DistanceCalculator.calculateDistance(order.getPickup(), distanceCourier)
										* 1000 <= distanceSlot * 2);
				firstOrders = Stream.concat(firstOrders, secondOrders);
				break;
			case "vip":
				firstOrders = streamSupplier.get().filter(order -> order.getVip().equals(Boolean.TRUE));
				break;
			case "food":
				firstOrders = streamSupplier.get().filter(order -> order.getFood().equals(Boolean.TRUE));
				break;
			default:
				firstOrders = streamSupplier.get();
				break;
			}
			orderedOrders
					.addAll(firstOrders.filter(order -> !orderedOrders.contains(order)).collect(Collectors.toList()));
		}
		orderedOrders.addAll(
				findByCourier.stream().filter(order -> !orderedOrders.contains(order)).collect(Collectors.toList()));
		return orderedOrders;
	}
}
