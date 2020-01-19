package code.challenge.rubicon.services.validator;

import java.util.Optional;

import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;

public interface IWaterOrderValidator {
    public Optional<String> checkOrderValidity(WaterOrder waterOrder, WaterOrderRequestAction action);
}