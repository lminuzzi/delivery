package com.glovoapp.backender;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OrderRepositoryTest {
    @Test
    void findAll() {
        List<Order> orders = new OrderRepository().findAll();

        assertFalse(orders.isEmpty());

        Order firstOrder = orders.get(0);

        Order expected = new Order().withId("order-1")
                .withDescription("I want a pizza cut into very small slices")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.3965463, 2.1963997))
                .withDelivery(new Location(41.407834, 2.1675979));

        assertEquals(expected, firstOrder);
    }

    @Test
    void findAllByCourierWithoutBox() {
        Courier courierFilter = new CourierRepository().findById("courier-4");
        List<Order> orders = new OrderRepository().findByCourier(courierFilter, "pizza,keys", 5);

        assertFalse(orders.isEmpty());

        Order firstOrder = orders.get(0);

        Order expected = new Order().withId("order-2")
                .withDescription("I want a hot dog")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.38773998322059, 2.163528682751925))
                .withDelivery(new Location(41.39392664893556, 2.1822508301080576));
                
        assertEquals(expected, firstOrder);
    }

    @Test
    void findAllByCourierNotLongDistance() {
        Courier courierFilter = new CourierRepository().findById("courier-5");
        List<Order> orders = new OrderRepository().findByCourier(courierFilter, "pizza,keys", 5);

        assertFalse(orders.isEmpty());

        Order firstOrder = orders.get(0);

        Order expected = new Order().withId("order-1")
                .withDescription("I want a pizza cut into very small slices")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.3965463, 2.1963997))
                .withDelivery(new Location(41.407834, 2.1675979));
                
        assertEquals(expected, firstOrder);
    }

    @Test
    void findAllByCourierNotLongDistanceAndWithoutBox() {
        Courier courierFilter = new CourierRepository().findById("courier-2");
        List<Order> orders = new OrderRepository().findByCourier(courierFilter, "pizza,keys,envelope", 6);

        assertFalse(orders.isEmpty());

        Order firstOrder = orders.get(0);

        Order expected = new Order().withId("order-2")
                .withDescription("I want a hot dog")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.38773998322059, 2.163528682751925))
                .withDelivery(new Location(41.39392664893556, 2.1822508301080576));
                
        assertEquals(expected, firstOrder);
    }

    @Test
    void findAllByCourier() {
        Courier courierFilter = new CourierRepository().findById("courier-3");
        List<Order> orders = new OrderRepository().findByCourier(courierFilter, "pizza,salad", 8);

        assertFalse(orders.isEmpty());

        Order firstOrder = orders.get(0);

        Order expected = new Order().withId("order-1")
                .withDescription("I want a pizza cut into very small slices")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.3965463, 2.1963997))
                .withDelivery(new Location(41.407834, 2.1675979));
                
        assertEquals(expected, firstOrder);
    }

    @Test
    void orderByCloseVipFood() {
        Courier courierFilter = new CourierRepository().findById("courier-3");
        List<Order> orders = new OrderRepository().findByCourier(courierFilter, "pizza,salad", 8);
        assertFalse(orders.isEmpty());
        int lengthOrders = orders.size();
        List<Order> sortedOrders = new OrderRepository().orderByPriorities(orders, "close,vip,food", 
            500, courierFilter.getLocation());
        assertFalse(sortedOrders.isEmpty());
        int lengthSortedOrders = sortedOrders.size();

        assertEquals(lengthOrders, lengthSortedOrders);

        Order firstOrder = sortedOrders.get(0);

        Order expected = new Order().withId("order-7")
                .withDescription("I want a newspapper")
                .withFood(false)
                .withVip(false)
                .withPickup(new Location(41.407290103152775, 2.1737712291996045))
                .withDelivery(new Location(41.387576370461375, 2.1842450379999554));
        
        assertEquals(expected, firstOrder);
    }

    @Test
    void orderByVipCloseFood() {
        Courier courierFilter = new CourierRepository().findById("courier-3");
        List<Order> orders = new OrderRepository().findByCourier(courierFilter, "pizza,salad", 8);
        assertFalse(orders.isEmpty());
        int lengthOrders = orders.size();
        List<Order> sortedOrders = new OrderRepository().orderByPriorities(orders, "vip,close,food", 
            500, courierFilter.getLocation());
        assertFalse(sortedOrders.isEmpty());
        int lengthSortedOrders = sortedOrders.size();

        assertEquals(lengthOrders, lengthSortedOrders);

        Order firstOrder = sortedOrders.get(0);

        Order expected = new Order().withId("order-4")
                .withDescription("Envelope")
                .withFood(true)
                .withVip(true)
                .withPickup(new Location(41.37790607439139, 2.1801331715968426))
                .withDelivery(new Location(41.380661712089115, 2.1760928408928155));
                
        assertEquals(expected, firstOrder);
    }

    @Test
    void orderByFoodVipClose() {
        Courier courierFilter = new CourierRepository().findById("courier-2");
        List<Order> orders = new OrderRepository().findByCourier(courierFilter, "pizza,salad", 8);
        assertFalse(orders.isEmpty());
        int lengthOrders = orders.size();
        List<Order> sortedOrders = new OrderRepository().orderByPriorities(orders, "food,vip,close", 
            100, courierFilter.getLocation());
        assertFalse(sortedOrders.isEmpty());
        int lengthSortedOrders = sortedOrders.size();

        assertEquals(lengthOrders, lengthSortedOrders);

        Order firstOrder = sortedOrders.get(0);

        Order expected = new Order().withId("order-2")
                .withDescription("I want a hot dog")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.38773998322059, 2.163528682751925))
                .withDelivery(new Location(41.39392664893556, 2.1822508301080576));

        assertEquals(expected, firstOrder);
    }
}