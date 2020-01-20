package code.challenge.rubicon.services.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;

import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;
import code.challenge.rubicon.services.repository.IWaterOrderReadonlyRepository;

/**
 * As a IValidityChecker, this class check if the order in cancellation request
 * is allowed to be cancelled based on it's status. Currently we don't allow if
 * delivery has been started for the order.
 */
@Component
public class CancelActionValidator implements IValidityChecker {

    public CancelActionValidator(IWaterOrderReadonlyRepository orderRetriever) {
    }

    public Optional<String> checkValidity(WaterOrder waterOrder, WaterOrderRequestAction action) {
        // This checker is only interested in CANCEL action.
        if (action == WaterOrderRequestAction.CANCEL && waterOrder.getStatus() != WaterOrder.OrderStatus.REQUESTED) {
            // Order cannot be cancelled once the order started being delievered.
            return Optional.of(String.format(
                    "Order cannot be cancelled. Current status of the order: %s"
                            + ", orderId: %s, startDateTime: %s, duration: %s",
                    waterOrder.getStatus(), waterOrder.getOrderId(), waterOrder.getStartDateTime(),
                    waterOrder.getDuration()));
        } else {
            return Optional.empty();
        }
    }
}