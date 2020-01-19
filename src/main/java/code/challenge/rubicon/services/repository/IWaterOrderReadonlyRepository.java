package code.challenge.rubicon.services.repository;

import java.util.List;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;

public interface IWaterOrderReadonlyRepository {

    public List<WaterOrder> getAllOrders();

    public WaterOrder getWaterOrderByOrderId(String orderId) throws OrderNotFoundException;

    public List<WaterOrder> getWaterOrderByFarmrId(String farmId) throws OrderNotFoundException;
}