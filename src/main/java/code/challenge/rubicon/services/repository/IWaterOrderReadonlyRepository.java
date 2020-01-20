package code.challenge.rubicon.services.repository;

import java.util.List;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;

/**
 * Provide access to orders with read-only privilege.
 */
public interface IWaterOrderReadonlyRepository {

    /**
     * Return all orders in the repository.
     *
     * @return List of all orders
     */
    public List<WaterOrder> getAllOrders();

    /**
     * Get order of given order id.
     *
     * @param orderId Order id to search.
     * @return WaterOrder of the given order id.
     * @throws OrderNotFoundException If order cannot be found,
     *                                OrderNotFoundException is thrown.
     */
    public WaterOrder getWaterOrderByOrderId(String orderId) throws OrderNotFoundException;

    /**
     * Get list of orders with given farm id.
     *
     * @param farmId Farm id to search.
     * @return List of orders with given farm id.
     * @throws OrderNotFoundException If order cannot be found,
     *                                OrderNotFoundException is thrown.
     */
    public List<WaterOrder> getWaterOrderByFarmrId(String farmId) throws OrderNotFoundException;
}