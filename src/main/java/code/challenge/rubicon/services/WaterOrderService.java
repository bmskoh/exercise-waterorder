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

/**
 * As a simple entry point of service layer, this class knows what to do for
 * each request using IWaterOrderRepository, IWaterOrderValidator and
 * IWaterOrderDeliveryScheduler based on the required business logic.
 */
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

    /**
     * Add a new water order. Do validtion before actually adding an order and
     * schedule it.
     */
    @Override
    public WaterOrder addWaterOrder(WaterOrder waterOrder) throws OrderValidityException {

        // 1. Do validation.
        Optional<String> validationErrMsg = this.orderValidator.checkOrderValidity(waterOrder,
                WaterOrderRequestAction.CREATE);
        if (validationErrMsg.isPresent()) {
            // This means that validation failed so an error message is present.
            throw new OrderValidityException(WaterOrderRequestAction.CREATE, validationErrMsg.get());
        }

        // 2. Add order in repository.
        WaterOrder createdOrder = this.repository.addWaterOrder(waterOrder);

        // 3. Ask scheduler to schedule delivery.
        this.deliveryScheduler.addDeliverySchedule(createdOrder);
        return createdOrder;
    }

    /**
     * Cancel existing water order. Do validation first to make sure there's no
     * validity rule violation. And ask scheduler to cancel the scheduled delivery
     * then cancel it in repository.
     */
    @Override
    public WaterOrder cancelWaterOrder(String orderId)
            throws OrderNotFoundException, OrderValidityException, DeliveryTaskNotFoundException {

        // 1. Do validation.
        WaterOrder waterOrder = this.repository.getWaterOrderByOrderId(orderId);
        Optional<String> validationErrMsg = this.orderValidator.checkOrderValidity(waterOrder,
                WaterOrderRequestAction.CANCEL);
        if (validationErrMsg.isPresent()) {
            throw new OrderValidityException(WaterOrderRequestAction.CANCEL, validationErrMsg.get());
        }

        // 2. Ask scheduler to cancel the schedule.
        this.deliveryScheduler.cancelDeliverySchdule(orderId);

        // 3. Cancel in repository.
        return this.repository.cancelWaterOrder(orderId);
    }

    /**
     * Return order list as it's given from repository.
     */
    @Override
    public List<WaterOrder> getAllOrders() {
        return this.repository.getAllOrders();
    }

    /**
     * Search order by orderId. Just call repository.
     */
    @Override
    public WaterOrder getWaterOrderByOrderId(String orderId) throws OrderNotFoundException {
        return this.repository.getWaterOrderByOrderId(orderId);
    }

    /**
     * Search order by farmId. Just call repository.
     */
    @Override
    public List<WaterOrder> getWaterOrderByFarmrId(String farmId) throws OrderNotFoundException {
        return this.repository.getWaterOrderByFarmrId(farmId);
    }

}