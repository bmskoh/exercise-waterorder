package code.challenge.rubicon.services.scheduler;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;

/**
 * Provide funtionality to add/cancel for the given order/orderid.
 */
public interface IWaterOrderDeliveryScheduler {
    /**
     * Add a new delivery schedule for the given oder.
     *
     * @param waterOrder The order to be scheduled.
     */
    public void addDeliverySchedule(WaterOrder waterOrder);

    /**
     * Cancel existing delivery schedule for the given order id.
     *
     * @param orderId Order id of the delivery schedule to be cancelled
     * @throws DeliveryTaskNotFoundException If delivery schedule cannot be found,
     *                                       DeliveryTaskNotFoundExcpetion is
     *                                       thrown.
     * @throws OrderNotFoundException        If order cannot be found,
     *                                       OrderNotFoundException is thrown.
     */
    public void cancelDeliverySchdule(String orderId) throws DeliveryTaskNotFoundException, OrderNotFoundException;
}