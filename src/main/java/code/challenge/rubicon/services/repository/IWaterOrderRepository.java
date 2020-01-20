package code.challenge.rubicon.services.repository;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.orderstatus.IWaterOrderStatusHelper;

/**
 * Provide full access to repository by providing adding/cancelling methods on
 * top of read-only methods. IWaterOrderStatusHelper doesn't have to be a part
 * of repositroy but let the repository implements IWaterOrderStatusHelper
 * functionality in this exercise.
 */
public interface IWaterOrderRepository extends IWaterOrderReadonlyRepository, IWaterOrderStatusHelper {

    /**
     * Add a new water order in repository.
     *
     * @param waterOrder New WaterOrder to add.
     * @return Newly added WaterOrder.
     */
    public WaterOrder addWaterOrder(WaterOrder waterOrder);

    /**
     * Cancel existing order in repository.
     *
     * @param orderId Order id of the order to be cancelled.
     * @return Cancelled order.
     * @throws OrderNotFoundException If order cannot be found,
     *                                OrderNotFoundException is thrown.
     */
    public WaterOrder cancelWaterOrder(String orderId) throws OrderNotFoundException;

}