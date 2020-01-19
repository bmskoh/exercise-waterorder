package code.challenge.rubicon.services.validator;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;

/**
 * Validate WatorOrder. This class can include multiple IValidityCheckers and
 * each IValidityCheckers has it's own rule to validate given WaterOrder.
 *
 * As an excercise, we only have 1 IValidityChecker, which is
 * DeliveryTimeOverlapValidator, as of 16/01/2020.
 */
@Component
public class WaterOrderValidator implements IWaterOrderValidator {

    private List<IValidityChecker> validationCheckers;

    @Autowired
    public WaterOrderValidator(List<IValidityChecker> validators) {
        this.validationCheckers = validators;
    }

    /**
     *
     */
    public Optional<String> checkOrderValidity(WaterOrder waterOrder, WaterOrderRequestAction action) {
        for (IValidityChecker validityChecker : this.validationCheckers) {
            Optional<String> validationErrMsg = validityChecker.checkValidity(waterOrder, action);
            if (validationErrMsg.isPresent()) {
                return validationErrMsg;
            }
        }

        return Optional.empty();
    }
}