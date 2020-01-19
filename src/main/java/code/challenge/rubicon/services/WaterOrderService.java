package code.challenge.rubicon.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.exceptions.OrderValidityException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.validator.IWaterOrderValidator;
import code.challenge.rubicon.services.repository.IWaterOrderRepository;
import code.challenge.rubicon.services.scheduler.IWaterOrderDeliveryScheduler;

@Service
public class WaterOrderService implements IWaterOrderService {

    private IWaterOrderRepository repository;
    private IWaterOrderValidator orderValidator;
    private IWaterOrderDeliveryScheduler deliveryScheduler;

    public WaterOrderService(IWaterOrderRepository waterOrderRepository, IWaterOrderValidator orderValidator,
            IWaterOrderDeliveryScheduler deliveryScheduler) {
        this.repository = waterOrderRepository;
        this.orderValidator = orderValidator;
        this.deliveryScheduler = deliveryScheduler;
    }

    @Override
    public WaterOrder addWaterOrder(WaterOrder waterOrder) throws OrderValidityException {
        Optional<String> validationErrMsg = this.orderValidator.checkOrderValidity(waterOrder,
                WaterOrderRequestAction.CREATE);
        if (validationErrMsg.isPresent()) {
            // This means that validation failed so an error message is present.
            throw new OrderValidityException(WaterOrderRequestAction.CREATE, validationErrMsg.get());
        }
        WaterOrder createdOrder = this.repository.addWaterOrder(waterOrder);
        this.deliveryScheduler.addDeliverySchedule(createdOrder);
        return createdOrder;
    }

    @Override
    public WaterOrder cancelWaterOrder(String orderId)
            throws OrderNotFoundException, OrderValidityException, DeliveryTaskNotFoundException {
        WaterOrder waterOrder = this.repository.getWaterOrderByOrderId(orderId);
        Optional<String> validationErrMsg = this.orderValidator.checkOrderValidity(waterOrder,
                WaterOrderRequestAction.CANCEL);
        if (validationErrMsg.isPresent()) {
            throw new OrderValidityException(WaterOrderRequestAction.CANCEL, validationErrMsg.get());
        }
        this.deliveryScheduler.cancelDeliverySchdule(orderId);
        return this.repository.cancelWaterOrder(orderId);
    }

    @Override
    public List<WaterOrder> getAllOrders() {
        return this.repository.getAllOrders();
    }

    @Override
    public WaterOrder getWaterOrderByOrderId(String orderId) throws OrderNotFoundException {
        return this.repository.getWaterOrderByOrderId(orderId);
    }

    @Override
    public List<WaterOrder> getWaterOrderByFarmrId(String farmId) throws OrderNotFoundException {
        return this.repository.getWaterOrderByFarmrId(farmId);
    }

}