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
 * As an excercise, we only have 2 IValidityCheckers, which are
 * DeliveryTimeOverlapValidator and CancelActivityValidator, as of 20/01/2020.
 */
@Component
public class WaterOrderValidator implements IWaterOrderValidator {

    private List<IValidityChecker> validationCheckers;

    @Autowired
    public WaterOrderValidator(List<IValidityChecker> validators) {
        this.validationCheckers = validators;
    }

    /**
     * Do validation by calling checkValidity() of each IValidationChecker. If
     * there's any validation error message is returned, return it straight away
     * instead of calling all remaining IValidityChecker.
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