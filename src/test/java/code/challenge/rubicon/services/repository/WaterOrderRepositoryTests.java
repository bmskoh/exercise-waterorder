package code.challenge.rubicon.services.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;

public class WaterOrderRepositoryTests {

    private HashMapOrderRepository repository;

    private String farmdIdToFind = "TheFarm";
    private final WaterOrder order1 = new WaterOrder("", "farmId1", LocalDateTime.now(), Duration.ofSeconds(10),
            WaterOrder.OrderStatus.REQUESTED);
    private final WaterOrder order2 = new WaterOrder("", this.farmdIdToFind, LocalDateTime.now(),
            Duration.ofSeconds(10), WaterOrder.OrderStatus.REQUESTED);
    private final WaterOrder order3 = new WaterOrder("", this.farmdIdToFind, LocalDateTime.now().plusHours(1),
            Duration.ofSeconds(10), WaterOrder.OrderStatus.REQUESTED);

    @BeforeEach
    public void initEach() {
        this.repository = new HashMapOrderRepository();
    }

    @Test
    @DisplayName("getWaterOrderByOrderId returns the order that's added by addWaterOrder")
    public void testAddWaterOrderAndGetOrderByOrderId() throws OrderNotFoundException {
        WaterOrder newOrder = this.repository.addWaterOrder(this.order1);

        WaterOrder retrievedOrder = this.repository.getWaterOrderByOrderId(newOrder.getOrderId());

        assertThat(newOrder).isEqualTo(retrievedOrder);
    }

    @Test
    @DisplayName("getWaterOrderByOrderId throws OrderNotFoundException if the order can't be found")
    public void testgetWaterOrderByOrderIdThrowsExceptionIfOrderIsNotThere() {
        assertThatThrownBy(() -> this.repository.getWaterOrderByOrderId("unknownOrderId"))
                .isInstanceOf(OrderNotFoundException.class).hasMessage("Order ID 'unknownOrderId' doesn't exist.")
                .hasFieldOrPropertyWithValue("idName", "orderId");
    }

    @Test
    @DisplayName("getAllOrders returns all the orders that were added by addWaterOrder")
    public void testAddWaterOrderAndGetAllOrders() throws OrderNotFoundException {
        WaterOrder newOrder1 = this.repository.addWaterOrder(this.order1);
        WaterOrder newOrder2 = this.repository.addWaterOrder(this.order2);
        WaterOrder newOrder3 = this.repository.addWaterOrder(this.order3);

        List<WaterOrder> allOrders = this.repository.getAllOrders();

        assertThat(allOrders.size()).isEqualTo(3);
        assertThat(allOrders.contains(newOrder1)).isTrue();
        assertThat(allOrders.contains(newOrder2)).isTrue();
        assertThat(allOrders.contains(newOrder3)).isTrue();
    }

    @Test
    @DisplayName("getWaterOrderByFarmId returns all the orders matching the given farmId")
    public void testGetWaterOrderByFrameId() throws OrderNotFoundException {
        this.repository.addWaterOrder(this.order1);
        WaterOrder newOrder2 = this.repository.addWaterOrder(this.order2);
        WaterOrder newOrder3 = this.repository.addWaterOrder(this.order3);

        List<WaterOrder> orders = this.repository.getWaterOrderByFarmrId(this.farmdIdToFind);

        assertThat(orders.size()).isEqualTo(2);
        assertThat(orders.contains(newOrder2)).isTrue();
        assertThat(orders.contains(newOrder3)).isTrue();
    }

    @Test
    @DisplayName("getWaterOrderByFarmId  throws OrderNotFoundException if the order can't be found")
    public void testGetWaterOrderByFrameIdThrowsOrderNotFoundException() throws OrderNotFoundException {
        this.repository.addWaterOrder(this.order2);
        this.repository.addWaterOrder(this.order3);

        assertThatThrownBy(() -> this.repository.getWaterOrderByFarmrId("farmId1"))
                .isInstanceOf(OrderNotFoundException.class).hasMessageContaining("farmID 'farmId1' doesn't exist")
                .hasFieldOrPropertyWithValue("idName", "farmId");
    }

    @Test
    @DisplayName("cancelWaterOrder updates order's status to OrderStatus.CANCELLED")
    public void testCancelWaterOrderUpdatesOrderStatus() throws OrderNotFoundException {
        WaterOrder newOrder = this.repository.addWaterOrder(this.order1);

        WaterOrder cancelledOrder = this.repository.cancelWaterOrder(newOrder.getOrderId());
        WaterOrder retrievedOrder = this.repository.getWaterOrderByOrderId(newOrder.getOrderId());

        assertThat(cancelledOrder.getStatus()).isEqualTo(WaterOrder.OrderStatus.CANCELLED);
        assertThat(retrievedOrder.getStatus()).isEqualTo(WaterOrder.OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancelWaterOrder throws OrderNotFoundException if the order can't be found")
    public void testCancelWaterOrderThrowsExceptionIfOrderIsNotThere() {
        assertThatThrownBy(() -> this.repository.cancelWaterOrder("unknownOrderId"))
                .isInstanceOf(OrderNotFoundException.class).hasMessage("Order ID 'unknownOrderId' doesn't exist.")
                .hasFieldOrPropertyWithValue("idName", "orderId");
    }
}
