package code.challenge.rubicon.services.validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;
import code.challenge.rubicon.services.repository.IWaterOrderReadonlyRepository;

/**
 * As a IValidityChecker, this class check if given WaterOrder has any
 * overlapping delivery time range with existing orders for the farm.
 */
@Component
public class DeliveryTimeOverlapValidator implements IValidityChecker {

    private IWaterOrderReadonlyRepository orderRetriever;

    private Logger logger = LoggerFactory.getLogger(DeliveryTimeOverlapValidator.class);

    public DeliveryTimeOverlapValidator(IWaterOrderReadonlyRepository orderRetriever) {
        this.orderRetriever = orderRetriever;
    }

    public Optional<String> checkValidity(WaterOrder waterOrder, WaterOrderRequestAction action) {
        // This validator is only interested in CREATE action.
        if (action == WaterOrderRequestAction.CREATE) {
            try {
                Optional<WaterOrder> overlappingOrder = null;
                // Get all orders for the farm and check if there's any delivery time
                // overlapping.
                synchronized(this.orderRetriever) {
                    List<WaterOrder> orders = this.orderRetriever.getWaterOrderByFarmrId(waterOrder.getFarmId());
                    overlappingOrder = orders.stream().filter(order -> {
                        return this.isTimeOverlap(order, waterOrder);
                    }).findAny();
                }

                if (overlappingOrder.isPresent()) {
                    WaterOrder existingOrder = overlappingOrder.get();

                    return Optional.of(String.format(
                            "Delivery time of the new order overlaps existing order's delivery time. "
                                    + "Existing order's orderId: %s, startDateTime: %s, duration: %s",
                            existingOrder.getOrderId(), existingOrder.getStartDateTime(), existingOrder.getDuration()));
                }
            } catch (OrderNotFoundException ex) {
                // This is not a problem in this IValidityChecker's view. Just log it.
                this.logger
                        .info(String.format("Couldn't find orders for the given farm id '%s'", waterOrder.getFarmId()));
            }
        }
        return Optional.empty();
    }

    /**
     * Test if given 2 orders' delivery time ranges overlap each other.
     */
    private boolean isTimeOverlap(WaterOrder waterOrder1, WaterOrder waterOrder2) {
        LocalDateTime startTime1 = waterOrder1.getStartDateTime();
        LocalDateTime endTime1 = startTime1.plus(waterOrder1.getDuration());
        LocalDateTime startTime2 = waterOrder2.getStartDateTime();
        LocalDateTime endTime2 = startTime2.plus(waterOrder2.getDuration());

        // If any of startTime1 or endTime1 is between startTime2 and endTime2,
        // then it means these orders' time overlap.
        if ((startTime1.compareTo(startTime2) >= 0 && startTime1.compareTo(endTime2) <= 0)
                || (endTime1.compareTo(startTime2) >= 0 && endTime1.compareTo(endTime2) <= 0)) {
            return true;
        }
        return false;
    }
}