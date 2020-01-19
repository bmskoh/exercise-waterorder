package code.challenge.rubicon.services.repository;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.orderstatus.IWaterOrderStatusHelper;

public interface IWaterOrderRepository extends IWaterOrderReadonlyRepository, IWaterOrderStatusHelper {

    public WaterOrder addWaterOrder(WaterOrder waterOrder);

    public WaterOrder cancelWaterOrder(String orderId) throws OrderNotFoundException;

}