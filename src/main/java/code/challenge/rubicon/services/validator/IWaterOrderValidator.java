package code.challenge.rubicon.services.validator;

import java.util.Optional;

import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;

/**
 * Provide order validation.
 */
public interface IWaterOrderValidator {
    /**
     * Check validity of the waterOrder for the action.
     * 
     * @param waterOrder WaterOrder to be validated
     * @param action     Action of the validation scenartio
     * @return Optional of validation message if there's any violation. Empty
     *         Optional otherwise.
     */
    public Optional<String> checkOrderValidity(WaterOrder waterOrder, WaterOrderRequestAction action);
}