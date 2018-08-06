package com.glovoapp.backender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
class API {
	private final String welcomeMessage;
	private final long distanceSlot;
	private final String hideDescriptionWords;
	private final long hideDistance;
	private final String priorities;
	private final OrderRepository orderRepository;
	private final CourierRepository courierRepository;

	@Autowired
	API(@Value("${backender.welcome_message}") String welcomeMessage,
			@Value("${parameter.distance.slot}") long distanceSlot,
			@Value("${parameter.hide.description.words}") String hideDescriptionWords,
			@Value("${parameter.hide.distance}") long hideDistance, @Value("${parameter.priorities}") String priorities,
			OrderRepository orderRepository, CourierRepository courierRepository) {
		this.welcomeMessage = welcomeMessage;
		this.distanceSlot = distanceSlot;
		this.hideDescriptionWords = hideDescriptionWords;
		this.hideDistance = hideDistance;
		this.priorities = priorities;
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
	}

	@RequestMapping("/")
	@ResponseBody
	String root() {
		return welcomeMessage;
	}

	@RequestMapping("/orders")
	@ResponseBody
	List<OrderVM> orders() {
		return orderRepository.findAll().stream().map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/orders/{courierId}", method = RequestMethod.GET)
	@ResponseBody
	List<OrderVM> courierOrders(@PathVariable("courierId") String courierId) {
		Courier courier = courierRepository.findById(courierId);
		if (courier == null) {
			return new ArrayList<OrderVM>();
		}
		List<Order> orderedOrdersByCourier = orderRepository.orderByPriorities(
				orderRepository.findByCourier(courier, hideDescriptionWords, hideDistance), priorities, distanceSlot,
				courier.getLocation());

		return orderedOrdersByCourier.stream().map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		SpringApplication.run(API.class);
	}
}
