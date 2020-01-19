package code.challenge.rubicon.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.exceptions.OrderValidityException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.IWaterOrderService;
import code.challenge.rubicon.services.WaterOrderRequestAction;

public class WaterOrderControllerTests {
    private IWaterOrderService waterOrderService;
    private WaterOrderController orderController;

    private List<WaterOrder> multipleOrderRows;
    private List<WaterOrder> singleOrderRow;

    private final String fakeFarmId = "FARMID1";
    private final String fakeOrderId = "ORDERID1";

    private final WaterOrder order1 = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
            WaterOrder.OrderStatus.REQUESTED);
    private final WaterOrder order2 = new WaterOrder("orderId2", "farmId2", LocalDateTime.now(), Duration.ofSeconds(10),
            WaterOrder.OrderStatus.REQUESTED);
    private final WaterOrder order3 = new WaterOrder("orderId3", "farmId3", LocalDateTime.now(), Duration.ofSeconds(10),
            WaterOrder.OrderStatus.REQUESTED);

    @BeforeEach
    public void initEach() throws OrderNotFoundException {
        this.waterOrderService = mock(IWaterOrderService.class);
        this.orderController = new WaterOrderController(this.waterOrderService);

        this.multipleOrderRows = new ArrayList<>();
        this.singleOrderRow = new ArrayList<>();

        this.multipleOrderRows.add(order1);
        this.multipleOrderRows.add(order2);

        this.singleOrderRow.add(order3);

        when(this.waterOrderService.getAllOrders()).thenReturn(this.multipleOrderRows);
        when(this.waterOrderService.getWaterOrderByFarmrId(this.fakeFarmId)).thenReturn(this.singleOrderRow);
        when(this.waterOrderService.getWaterOrderByOrderId(this.fakeOrderId)).thenReturn(order2);
    }

    @Test
    @DisplayName("getWaterOrders returns what WaterOrderService's getAllOrders returns if there's no farmdid parameter is given.")
    public void testGetWaterOrdersNoFarmIDParameter() throws OrderNotFoundException {
        final List<WaterOrder> orders = this.orderController.getWaterOrders(null);

        assertThat(orders.size()).isEqualTo(2);
        assertThat(orders.get(0).getOrderId()).isEqualTo(this.order1.getOrderId());
        assertThat(orders.get(1).getOrderId()).isEqualTo(this.order2.getOrderId());
    }

    @Test
    @DisplayName("getWaterOrders returns what WaterOrderService'sgetWaterOrderByFarmrId returns if farmdid parameter is given.")
    public void testGetWaterOrdersFarmIDParameterGiven() throws OrderNotFoundException {
        final List<WaterOrder> orders = this.orderController.getWaterOrders(this.fakeFarmId);

        assertThat(orders.size()).isEqualTo(1);
        assertThat(orders.get(0).getOrderId()).isEqualTo(this.order3.getOrderId());
    }

    @Test
    @DisplayName("getWaterOrder returns what WaterOrderService'sgetWaterOrderByFarmrId returns.")
    public void testGetWaterOrderByOrderId() throws OrderNotFoundException {
        final WaterOrder order = this.orderController.getWaterOrder(this.fakeOrderId);

        assertThat(order.getOrderId()).isEqualTo(this.order2.getOrderId());
    }

    @Test
    @DisplayName("addWaterOrder calls WaterOrderService's addWaterOrder methodand return accordingly.")
    public void testAddWaterOrder() throws OrderValidityException {
        when(waterOrderService.addWaterOrder(this.order3)).thenReturn(this.order1);

        final WaterOrder returnedOrder = this.orderController.addWaterOrder(this.order3);

        verify(waterOrderService).addWaterOrder(eq(this.order3));
        assertThat(returnedOrder.getOrderId()).isEqualTo(this.order1.getOrderId());
    }

    @Test
    @DisplayName("cancelWaterOrder calls WaterOrderService's cancelWaterOrdermethod and return accordingly.")
    public void testCancelWaterOrder()
            throws OrderNotFoundException, OrderValidityException, DeliveryTaskNotFoundException {
        when(waterOrderService.cancelWaterOrder(this.order1.getOrderId())).thenReturn(this.order1);

        this.orderController.cancelWaterOrder(this.order1.getOrderId());

        verify(waterOrderService).cancelWaterOrder(eq(this.order1.getOrderId()));
    }

    @Test
    @DisplayName("Test OrderNotFoundException's reponse body includes orderId anderror message")
    public void testOrderNotFoundHandler() {
        final String exId = "exId";
        final String exMsg = "This is an exception";
        final OrderNotFoundException exception = new OrderNotFoundException(exId, exMsg);

        final Map<String, String> errorMap = this.orderController.handleOrderNotFoundException(exception);

        assertThat(errorMap.get(exId)).isEqualTo(exMsg);
    }

    @Test
    @DisplayName("Test OrderValidityException's reponse body includes action anderror message")
    public void testOrderValidityExceptionHandler() {
        final WaterOrderRequestAction exAction = WaterOrderRequestAction.CREATE;
        final String exMsg = "This is an exception";
        final OrderValidityException exception = new OrderValidityException(exAction, exMsg);

        final Map<String, String> errorMap = this.orderController.handleOrderValidityException(exception);

        assertThat(errorMap.get(exAction.toString())).isEqualTo(exMsg);
    }

    @Test
    @DisplayName("Test OrderValidityException's reponse body includes action anderror message")
    public void testDurationValidator() {
        Errors errors = mock(Errors.class);
        WaterOrderController.DurationValidator validator = this.orderController.new DurationValidator();

        WaterOrder wOrder = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(-10),
                WaterOrder.OrderStatus.REQUESTED);
        validator.validate(wOrder, errors);

        verify(errors).rejectValue("duration", "Duration cannot be negative.");
    }

    @Test
    @DisplayName("Duration Validator handles WaterOrder class")
    public void testDurationValidatorHandlesWaterOrder() {
        WaterOrderController.DurationValidator validator = this.orderController.new DurationValidator();

        assertThat(validator.supports(WaterOrder.class)).isTrue();
    }

    @Test
    @DisplayName("Duration Validator rejectValue if duation is negative")
    public void testDurationValidatorNegativeDuration() {
        Errors errors = mock(Errors.class);
        WaterOrderController.DurationValidator validator = this.orderController.new DurationValidator();

        WaterOrder wOrder = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(-10),
                WaterOrder.OrderStatus.REQUESTED);
        validator.validate(wOrder, errors);

        verify(errors).rejectValue("duration", "Duration cannot be negative.");
    }

    @Test
    @DisplayName("Duration Validator doesn't call rejectValue if duation is positive")
    public void testDurationValidatorNonNegativeDuration() {
        Errors errors = mock(Errors.class);
        WaterOrderController.DurationValidator validator = this.orderController.new DurationValidator();

        validator.validate(this.order1, errors);

        verify(errors, never()).rejectValue("duration", "Duration cannot be negative.");
    }
}