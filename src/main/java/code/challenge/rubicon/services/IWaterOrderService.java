package code.challenge.rubicon.services;

import java.util.List;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.exceptions.OrderValidityException;
import code.challenge.rubicon.model.WaterOrder;

public interface IWaterOrderService {
    public WaterOrder addWaterOrder(WaterOrder waterOrder) throws OrderValidityException;

    public WaterOrder cancelWaterOrder(String orderId)
            throws OrderNotFoundException, OrderValidityException, DeliveryTaskNotFoundException;

    public List<WaterOrder> getAllOrders();

    public WaterOrder getWaterOrderByOrderId(String orderId) throws OrderNotFoundException;

    public List<WaterOrder> getWaterOrderByFarmrId(String farmId) throws OrderNotFoundException;
}