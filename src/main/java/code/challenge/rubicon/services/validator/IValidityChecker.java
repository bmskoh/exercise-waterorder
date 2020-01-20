package code.challenge.rubicon.services.validator;

import java.util.Optional;

import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;

/**
 * IValdityChecker provides validation by checking whether or not WaterOrder has
 * any validity violation.
 */
public interface IValidityChecker {
    /**
     * Check validity of the waterOrder for the action
     * 
     * @param waterOrder WaterOrder to be validated
     * @param action
     * @return Optional of validation message if there's any violation. Empty
     *         Optional otherwise.
     */
    public Optional<String> checkValidity(WaterOrder waterOrder, WaterOrderRequestAction action);
}