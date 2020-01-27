package code.challenge.rubicon.services.repository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.model.WaterOrder.OrderStatus;

/**
 * Implementation of repository interface. Use a simple HashMap as repository.
 */
@Repository
public class HashMapOrderRepository implements IWaterOrderRepository {

    private final Map<String, WaterOrder> waterOrders = new HashMap<>();

    /**
     * Add a new order. Generate a new order id and set it to the order.
     */
    @Override
    public WaterOrder addWaterOrder(WaterOrder waterOrder) {
        // Generate order id for the new order.
        String orderId = this.generateOrderId(waterOrder);
        synchronized(this) {
            WaterOrder newOrder = new WaterOrder(orderId, waterOrder.getFarmId(), waterOrder.getStartDateTime(),
                    waterOrder.getDuration(), WaterOrder.OrderStatus.REQUESTED);
            this.waterOrders.put(orderId, newOrder);
            return newOrder;
        }
    }

    /**
     * Cancel order.
     */
    @Override
    public synchronized WaterOrder cancelWaterOrder(String orderId) throws OrderNotFoundException {
        WaterOrder waterOrder = this.waterOrders.get(orderId);
        if (waterOrder != null) {
            this.updateOrderstatus(orderId, WaterOrder.OrderStatus.CANCELLED);
            return waterOrder;
        } else {
            throw new OrderNotFoundException("orderId", String.format("Order ID '%s' doesn't exist.", orderId));
        }
    }

    /**
     * Return all orders in repository.
     */
    @Override
    public synchronized List<WaterOrder> getAllOrders() {
        return new ArrayList<WaterOrder>(this.waterOrders.values());
    }

    /**
     * Find order by order id.
     */
    @Override
    public WaterOrder getWaterOrderByOrderId(String orderId) throws OrderNotFoundException {
        WaterOrder waterOrder = null;
        synchronized(this) {
            waterOrder = this.waterOrders.get(orderId);
        }
        if (waterOrder == null) {
            throw new OrderNotFoundException("orderId", String.format("Order ID '%s' doesn't exist.", orderId));
        }
        return waterOrder;
    }

    /**
     * Find orders by farm id.
     */
    @Override
    public List<WaterOrder> getWaterOrderByFarmrId(String farmId) throws OrderNotFoundException {
        List<WaterOrder> orders = null;
        synchronized(this) {
            orders = this.waterOrders.values().stream().filter(order -> order.getFarmId().equals(farmId))
                    .collect(Collectors.toList());
        }

        if (orders.size() == 0) {
            throw new OrderNotFoundException("farmId", String.format("Order for farmID '%s' doesn't exist.", farmId));
        }

        return orders;
    }

    /**
     * Update order's status.
     */
    @Override
    public synchronized void updateOrderstatus(String orderId, OrderStatus status) throws OrderNotFoundException {
        WaterOrder existingOrder = this.getWaterOrderByOrderId(orderId);
        existingOrder.setStatus(status);
    }

    /**
     * Generate order id. It's a combination of farmid and startdatetime.
     */
    private String generateOrderId(WaterOrder waterOrder) {
        // e.g. "MYFARM:20200116101010"
        return waterOrder.getFarmId() + ":"
                + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(waterOrder.getStartDateTime());
    }

}