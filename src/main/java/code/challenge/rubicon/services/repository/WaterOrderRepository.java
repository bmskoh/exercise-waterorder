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

@Repository
public class WaterOrderRepository implements IWaterOrderRepository {

    private Map<String, WaterOrder> waterOrders = new HashMap<>();

    @Override
    public WaterOrder addWaterOrder(WaterOrder waterOrder) {
        String orderId = this.generateOrderId(waterOrder);
        WaterOrder newOrder = new WaterOrder(orderId, waterOrder.getFarmId(), waterOrder.getStartDateTime(),
                waterOrder.getDuration(), WaterOrder.OrderStatus.REQUESTED);
        this.waterOrders.put(orderId, newOrder);
        return newOrder;
    }

    @Override
    public WaterOrder cancelWaterOrder(String orderId) throws OrderNotFoundException {
        WaterOrder waterOrder = this.waterOrders.get(orderId);
        if (waterOrder != null) {
            this.updateOrderstatus(orderId, WaterOrder.OrderStatus.CANCELLED);
            return waterOrder;
        } else {
            throw new OrderNotFoundException("orderId", String.format("Order ID '%s' doesn't exist.", orderId));
        }
    }

    @Override
    public List<WaterOrder> getAllOrders() {
        return new ArrayList<WaterOrder>(this.waterOrders.values());
    }

    @Override
    public WaterOrder getWaterOrderByOrderId(String orderId) throws OrderNotFoundException {
        WaterOrder waterOrder = this.waterOrders.get(orderId);
        if (waterOrder == null) {
            throw new OrderNotFoundException("orderId", String.format("Order ID '%s' doesn't exist.", orderId));
        }
        return waterOrders.get(orderId);
    }

    @Override
    public List<WaterOrder> getWaterOrderByFarmrId(String farmId) throws OrderNotFoundException {
        List<WaterOrder> orders = this.waterOrders.values().stream().filter(order -> order.getFarmId().equals(farmId))
                .collect(Collectors.toList());

        if (orders.size() == 0) {
            throw new OrderNotFoundException("farmId", String.format("Order for farmID '%s' doesn't exist.", farmId));
        }

        return orders;
    }

    @Override
    public void updateOrderstatus(String orderId, OrderStatus status) throws OrderNotFoundException {
        WaterOrder existingOrder = this.getWaterOrderByOrderId(orderId);
        existingOrder.setStatus(status);
    }

    private String generateOrderId(WaterOrder waterOrder) {
        // e.g. "MYFARM:20200116101010"
        return waterOrder.getFarmId() + ":"
                + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(waterOrder.getStartDateTime());
    }

}