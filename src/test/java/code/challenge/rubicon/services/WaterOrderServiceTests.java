package code.challenge.rubicon.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.exceptions.OrderValidityException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.repository.IWaterOrderRepository;
import code.challenge.rubicon.services.scheduler.IWaterOrderDeliveryScheduler;
import code.challenge.rubicon.services.validator.IWaterOrderValidator;

public class WaterOrderServiceTests {

    private IWaterOrderRepository repository;
    private IWaterOrderValidator validator;
    private IWaterOrderDeliveryScheduler scheduler;

    private WaterOrderService service;

    private final WaterOrder order1 = new WaterOrder("orderId1", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
            WaterOrder.OrderStatus.REQUESTED);
    private final WaterOrder order2 = new WaterOrder("orderId2", "farmId2", LocalDateTime.now(), Duration.ofSeconds(10),
            WaterOrder.OrderStatus.REQUESTED);

    private List<WaterOrder> multipleOrders;
    private List<WaterOrder> singleOrderInList;

    @BeforeEach
    public void initEach() throws OrderNotFoundException {
        this.repository = mock(IWaterOrderRepository.class);
        this.validator = mock(IWaterOrderValidator.class);
        this.scheduler = mock(IWaterOrderDeliveryScheduler.class);

        this.service = new WaterOrderService(this.repository, this.validator, this.scheduler);
        this.multipleOrders = new ArrayList<>();
        this.multipleOrders.add(this.order1);
        this.multipleOrders.add(this.order2);

        this.singleOrderInList = new ArrayList<>();
        this.singleOrderInList.add(this.order2);
    }

    @Test
    @DisplayName("addWaterOrder throws OrderValidityException if validation fails.")
    public void testAddWaterOrderWhenValidationFails() {
        String validationErrorMsg = "It's not valid";
        when(this.validator.checkOrderValidity(any(), any())).thenReturn(Optional.of(validationErrorMsg));

        assertThatThrownBy(() -> this.service.addWaterOrder(this.order1)).isInstanceOf(OrderValidityException.class)
                .hasMessage(validationErrorMsg).hasFieldOrPropertyWithValue("action", WaterOrderRequestAction.CREATE);
    }

    @Test
    @DisplayName("addWaterOrder calls IWaterOrderRepository and IWaterOrderDeliveryScheduler when validation pass.")
    public void testAddWaterOrderWhenValidationPass() throws OrderValidityException {
        when(this.validator.checkOrderValidity(any(), any())).thenReturn(Optional.empty());
        when(this.repository.addWaterOrder(any())).thenReturn(this.order2);

        this.service.addWaterOrder(this.order1);

        verify(this.repository).addWaterOrder(eq(this.order1));
        // Should have called scheduler with the WaterOrder that repository returned.
        verify(this.scheduler).addDeliverySchedule(eq(this.order2));
    }

    @Test
    @DisplayName("cancelWaterOrder throws OrderValidityException if validation fails.")
    public void testCancelWaterOrderWhenValidationFails() {
        String validationErrorMsg = "It's not valid";
        when(this.validator.checkOrderValidity(any(), any())).thenReturn(Optional.of(validationErrorMsg));

        assertThatThrownBy(() -> this.service.cancelWaterOrder("fakeId")).isInstanceOf(OrderValidityException.class)
                .hasMessage(validationErrorMsg).hasFieldOrPropertyWithValue("action", WaterOrderRequestAction.CANCEL);
    }

    @Test
    @DisplayName("cancelWaterOrder calls IWaterOrderRepository andIWaterOrderDeliveryScheduler when validation pass.")
    public void testCancelWaterOrderWhenValidationPass()
            throws OrderValidityException, OrderNotFoundException, DeliveryTaskNotFoundException {
        when(this.validator.checkOrderValidity(any(), any())).thenReturn(Optional.empty());

        String fakeId = "fakeId111";
        this.service.cancelWaterOrder(fakeId);

        verify(this.repository).cancelWaterOrder(eq(fakeId));
        // Should have called scheduler with the WaterOrder that repository returned.
        verify(this.scheduler).cancelDeliverySchdule(eq(fakeId));
    }

    @Test
    @DisplayName("getAllOrders returns what repository's getAllOrders returns.")
    public void testGetAllOrders() {
        when(this.repository.getAllOrders()).thenReturn(this.multipleOrders);

        List<WaterOrder> returnedOrders = this.service.getAllOrders();

        assertThat(returnedOrders.size()).isEqualTo(2);
        assertThat(returnedOrders.get(0).getOrderId()).isEqualTo("orderId1");
        assertThat(returnedOrders.get(1).getOrderId()).isEqualTo("orderId2");
    }

    @Test
    @DisplayName("getWaterOrderByOrderId returns what repository's getWaterOrderByOrderId returns.")
    public void testGetWaterOrderbyOrderId() throws OrderNotFoundException {
        String fakeId = "fakeId111";
        when(this.repository.getWaterOrderByOrderId(fakeId)).thenReturn(this.order1);

        WaterOrder order = this.service.getWaterOrderByOrderId(fakeId);

        verify(this.repository).getWaterOrderByOrderId(eq(fakeId));
        assertThat(order.getOrderId()).isEqualTo(this.order1.getOrderId());
    }

    @Test
    @DisplayName("getWaterOrderByFarmId returns what repository's getWaterOrderByFarmId returns.")
    public void testGetWaterOrderbyFarmId() throws OrderNotFoundException {
        String fakeId = "fakeId111";
        when(this.repository.getWaterOrderByFarmrId(fakeId)).thenReturn(this.singleOrderInList);

        List<WaterOrder> returnedOrders = this.service.getWaterOrderByFarmrId(fakeId);

        verify(this.repository).getWaterOrderByFarmrId(eq(fakeId));
        assertThat(returnedOrders.size()).isEqualTo(1);
        assertThat(returnedOrders.get(0).getOrderId()).isEqualTo("orderId2");
    }
}