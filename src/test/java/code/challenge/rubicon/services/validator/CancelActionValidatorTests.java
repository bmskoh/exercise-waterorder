package code.challenge.rubicon.services.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;
import code.challenge.rubicon.services.repository.IWaterOrderReadonlyRepository;

public class CancelActionValidatorTests {

    private IWaterOrderReadonlyRepository repository;

    private CancelActionValidator validator;

    @BeforeEach
    public void initEach() {
        this.repository = mock(IWaterOrderReadonlyRepository.class);

        this.validator = new CancelActionValidator(this.repository);
    }

    @Test
    @DisplayName("Valid for order in REQUESTED status.")
    public void testValidityViolationIfRequestedStatus() {
        WaterOrder order = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
                WaterOrder.OrderStatus.REQUESTED);
        Optional<String> validationMsg = this.validator.checkValidity(order, WaterOrderRequestAction.CANCEL);

        assertThat(validationMsg).isEmpty();
    }

    @Test
    @DisplayName("Invalid for order in IN_PROGRESS status.")
    public void testValidityViolationIfInProgressStatus() {
        WaterOrder order = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
                WaterOrder.OrderStatus.IN_PROGRESS);
        Optional<String> validationMsg = this.validator.checkValidity(order, WaterOrderRequestAction.CANCEL);

        assertThat(validationMsg.get()).contains("Order cannot be cancelled");
    }

    @Test
    @DisplayName("Invalid for order in DELIVERED status.")
    public void testValidityViolationIfDeliveredStatus() {
        WaterOrder order = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
                WaterOrder.OrderStatus.DELIVERED);
        Optional<String> validationMsg = this.validator.checkValidity(order, WaterOrderRequestAction.CANCEL);

        assertThat(validationMsg.get()).contains("Order cannot be cancelled");
    }

    @Test
    @DisplayName("Invalid for order in CANCELLED status.")
    public void testValidityViolationIfCancelledStatus() {
        WaterOrder order = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
                WaterOrder.OrderStatus.CANCELLED);
        Optional<String> validationMsg = this.validator.checkValidity(order, WaterOrderRequestAction.CANCEL);

        assertThat(validationMsg.get()).contains("Order cannot be cancelled");
    }

    @Test
    @DisplayName("Skip validation for CREATE action.")
    public void testCheckValidityNotInterestedOtherThanCancel() {
        WaterOrder order = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
                WaterOrder.OrderStatus.IN_PROGRESS);
        Optional<String> validationMsg = this.validator.checkValidity(order, WaterOrderRequestAction.CREATE);

        assertThat(validationMsg).isEmpty();
    }
}