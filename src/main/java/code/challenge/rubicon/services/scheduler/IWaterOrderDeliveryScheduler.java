package code.challenge.rubicon.services.scheduler;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;

public interface IWaterOrderDeliveryScheduler {
    public void addDeliverySchedule(WaterOrder waterOrder);

    public void cancelDeliverySchdule(String orderId) throws DeliveryTaskNotFoundException, OrderNotFoundException;
}