package code.challenge.rubicon.services;

import java.util.List;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.exceptions.OrderValidityException;
import code.challenge.rubicon.model.WaterOrder;

/**
 * Define service that provides a number of functionalities based on business
 * requirements.
 */
public interface IWaterOrderService {
    /**
     * Add a new water order.
     *
     * @param waterOrder New WaterOrder to add.
     * @return New WaterOrder. This would include order id.
     * @throws OrderValidityException If there's any validity violation,
     *                                OrderValidityException is thrown.
     */
    public WaterOrder addWaterOrder(WaterOrder waterOrder) throws OrderValidityException;

    /**
     * Cancel existing water order.
     *
     * @param orderId Order id of the order to cancel.
     * @return Cancelled WaterOrder.
     * @throws OrderNotFoundException        If order is not found for the order id,
     *                                       OrderNotFoundException is thrown.
     * @throws OrderValidityException        If there's any validity violation for
     *                                       cancel action. OverValidityException is
     *                                       thrown.
     * @throws DeliveryTaskNotFoundException If schedules delivery for the order id,
     *                                       DeliveryTaskNotFoundException is
     *                                       thrown.
     */
    public WaterOrder cancelWaterOrder(String orderId)
            throws OrderNotFoundException, OrderValidityException, DeliveryTaskNotFoundException;

    /**
     * Return a list of all orders.
     *
     * @return A list of all existing orders including cancelled orders.
     */
    public List<WaterOrder> getAllOrders();

    /**
     * Search order by orderId.
     * 
     * @param orderId Order id of the order to find
     * @return
     * @throws OrderNotFoundException If order for the order id cannot be found,
     *                                OrderNotFoundException is thrown.
     */
    public WaterOrder getWaterOrderByOrderId(String orderId) throws OrderNotFoundException;

    /**
     * Search order by farmId.
     * 
     * @param farmId Farm id of the orders to find
     * @return
     * @throws OrderNotFoundException If orders for the given farm id cannot be
     *                                found, OrderNotFoundException is thrown.
     */
    public List<WaterOrder> getWaterOrderByFarmrId(String farmId) throws OrderNotFoundException;
}