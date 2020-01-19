package code.challenge.rubicon.services.orderstatus;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;

/**
 *
 */
public interface IWaterOrderStatusHelper {
    public void updateOrderstatus(String orderId, WaterOrder.OrderStatus status) throws OrderNotFoundException;
}