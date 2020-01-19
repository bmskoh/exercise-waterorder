package code.challenge.rubicon.services.validator;

import java.util.Optional;

import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;

public interface IValidityChecker {
    public Optional<String> checkValidity(WaterOrder waterOrder, WaterOrderRequestAction action);
}