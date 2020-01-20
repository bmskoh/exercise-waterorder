package code.challenge.rubicon.services.orderstatus;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;

/**
 * Provide functionality to update order's status.
 */
public interface IWaterOrderStatusHelper {
    /**
     * Update order's status to given status.
     * 
     * @param orderId Order id of the order to update
     * @param status  New status of the order to set
     * @throws OrderNotFoundException If order cannot be found,
     *                                OrderNotFoundException is thrown.
     */
    public void updateOrderstatus(String orderId, WaterOrder.OrderStatus status) throws OrderNotFoundException;
}