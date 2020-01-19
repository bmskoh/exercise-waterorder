package code.challenge.rubicon.services.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.WaterOrderRequestAction;
import code.challenge.rubicon.services.repository.IWaterOrderReadonlyRepository;

public class DeliveryTimeOverlapValidatorTests {
    private IWaterOrderReadonlyRepository repository;

    private DeliveryTimeOverlapValidator validator;

    @BeforeEach
    public void initEach() {
        this.repository = mock(IWaterOrderReadonlyRepository.class);

        this.validator = new DeliveryTimeOverlapValidator(this.repository);
    }

    @Test
    @DisplayName("Given order's end time overlaps existing order's delivery duation")
    public void testpValidityViolationEndTimeOverlaps() throws OrderNotFoundException {
        WaterOrder existingOrder = new WaterOrder("orderId1", "farmId1", LocalDateTime.of(2020, 1, 1, 10, 10, 10),
                Duration.ofSeconds(60), WaterOrder.OrderStatus.REQUESTED);
        WaterOrder anotherDummyOrder = new WaterOrder("orderId1", "farmId2", LocalDateTime.of(2020, 1, 1, 10, 10, 10),
                Duration.ofSeconds(60), WaterOrder.OrderStatus.REQUESTED);
        ArrayList<WaterOrder> orders = new ArrayList<>();
        orders.add(existingOrder);
        orders.add(anotherDummyOrder);

        WaterOrder newOrder = new WaterOrder("orderId2", "farmId1", LocalDateTime.of(2020, 1, 1, 10, 10, 0),
                Duration.ofSeconds(30), WaterOrder.OrderStatus.REQUESTED);

        when(this.repository.getWaterOrderByFarmrId(any())).thenReturn(orders);

        Optional<String> validityMsg = this.validator.checkValidity(newOrder, WaterOrderRequestAction.CREATE);

        assertThat(validityMsg.get()).contains("Delivery time of the new order overlaps existing order");
    }

    @Test
    @DisplayName("Given order's start time overlaps existing order's delivery duation")
    public void testpValidityViolationStartTimeOverlaps() throws OrderNotFoundException {
        WaterOrder existingOrder = new WaterOrder("orderId1", "farmId1", LocalDateTime.of(2020, 1, 1, 10, 10, 10),
                Duration.ofSeconds(60), WaterOrder.OrderStatus.REQUESTED);
        ArrayList<WaterOrder> orders = new ArrayList<>();
        orders.add(existingOrder);

        WaterOrder newOrder = new WaterOrder("orderId2", "farmId1", LocalDateTime.of(2020, 1, 1, 10, 11, 0),
                Duration.ofSeconds(30), WaterOrder.OrderStatus.REQUESTED);

        when(this.repository.getWaterOrderByFarmrId(any())).thenReturn(orders);

        Optional<String> validityMsg = this.validator.checkValidity(newOrder, WaterOrderRequestAction.CREATE);

        assertThat(validityMsg.get()).contains("Delivery time of the new order overlaps existing order");
    }

    @Test
    @DisplayName("Given order's duration doesn't overlaps existing order's delivery duation")
    public void testpValidityViolationNoOverlaps() throws OrderNotFoundException {
        WaterOrder existingOrder = new WaterOrder("orderId1", "farmId1", LocalDateTime.of(2020, 1, 1, 10, 10, 10),
                Duration.ofSeconds(60), WaterOrder.OrderStatus.REQUESTED);
        ArrayList<WaterOrder> orders = new ArrayList<>();
        orders.add(existingOrder);

        WaterOrder newOrder = new WaterOrder("orderId2", "farmId1", LocalDateTime.of(2020, 1, 1, 10, 11, 30),
                Duration.ofSeconds(30), WaterOrder.OrderStatus.REQUESTED);

        when(this.repository.getWaterOrderByFarmrId(any())).thenReturn(orders);

        Optional<String> validityMsg = this.validator.checkValidity(newOrder, WaterOrderRequestAction.CREATE);

        assertThat(validityMsg).isEmpty();
    }

    @Test
    @DisplayName("Skip checking if it's not CREATE action")
    public void testpValidityViolationNoCreateAction() throws OrderNotFoundException {
        WaterOrder existingOrder = new WaterOrder("orderId1", "farmId1", LocalDateTime.of(2020, 1, 1, 10, 10, 10),
                Duration.ofSeconds(60), WaterOrder.OrderStatus.REQUESTED);
        ArrayList<WaterOrder> orders = new ArrayList<>();
        orders.add(existingOrder);

        // Duration overlaps
        WaterOrder newOrder = new WaterOrder("orderId2", "farmId1", LocalDateTime.of(2020, 1, 1, 10, 11, 0),
                Duration.ofSeconds(30), WaterOrder.OrderStatus.REQUESTED);

        when(this.repository.getWaterOrderByFarmrId(any())).thenReturn(orders);

        Optional<String> validityMsg = this.validator.checkValidity(newOrder, WaterOrderRequestAction.CANCEL);

        assertThat(validityMsg).isEmpty();
    }
}