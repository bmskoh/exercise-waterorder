package code.challenge.rubicon.services.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;

public class WaterOrderValidatorTests {

    private WaterOrderValidator validator;

    IValidityChecker checker1;
    IValidityChecker checker2;
    IValidityChecker checker3;

    private final WaterOrder order1 = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
            WaterOrder.OrderStatus.REQUESTED);

    @BeforeEach
    public void initEach() {
        this.checker1 = mock(IValidityChecker.class);
        this.checker2 = mock(IValidityChecker.class);
        this.checker3 = mock(IValidityChecker.class);

        List<IValidityChecker> validators = new ArrayList<>();

        validators.add(checker1);
        validators.add(checker2);
        validators.add(checker3);

        this.validator = new WaterOrderValidator(validators);
    }

    @Test
    @DisplayName("checkOrderValidity returns Optional validation error message if there's any checker returns message.")
    public void testCheckOrderValidityReturnForAnyInvalidity() {
        String validationErrorMsg = "Not valid.";
        when(checker1.checkValidity(any(), any())).thenReturn(Optional.empty());
        when(checker2.checkValidity(any(), any())).thenReturn(Optional.of(validationErrorMsg));
        when(checker3.checkValidity(any(), any())).thenReturn(Optional.empty());

        Optional<String> errorMsg = this.validator.checkOrderValidity(this.order1, WaterOrderRequestAction.CREATE);

        assertThat(errorMsg.get()).isEqualTo(validationErrorMsg);
    }

    @Test
    @DisplayName("checkOrderValidity returns empty Optional if there's no checker returns message.")
    public void testCheckOrderValidityReturnEmptyOptional() {
        when(checker1.checkValidity(any(), any())).thenReturn(Optional.empty());
        when(checker2.checkValidity(any(), any())).thenReturn(Optional.empty());
        when(checker3.checkValidity(any(), any())).thenReturn(Optional.empty());

        Optional<String> errorMsg = this.validator.checkOrderValidity(this.order1, WaterOrderRequestAction.CREATE);

        assertThat(errorMsg).isEmpty();
    }

    @Test
    @DisplayName("checkOrderValidity returns empty Optional if there's no checker.")
    public void testCheckOrderValidityReturnEmptyOptionalIfNoChecker() {
        this.validator = new WaterOrderValidator(new ArrayList<IValidityChecker>());

        Optional<String> errorMsg = this.validator.checkOrderValidity(this.order1, WaterOrderRequestAction.CREATE);

        assertThat(errorMsg).isEmpty();
    }
}