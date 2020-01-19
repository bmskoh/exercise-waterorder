package code.challenge.rubicon.services.scheduler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.orderstatus.IWaterOrderStatusHelper;

public class WaterOrderSchedulerTests {
    private WaterOrderDeliveryScheduler scheduler;

    private ScheduledExecutorService executorService;
    private IWaterOrderStatusHelper statusUpdater;

    private LocalDateTime FIXED_LOCAL_NOW = LocalDateTime.of(2020, 1, 10, 10, 10, 0);

    private Clock fixedClock = Clock.fixed(this.FIXED_LOCAL_NOW.atZone(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());

    @BeforeEach
    public void initEach() {
        this.statusUpdater = mock(IWaterOrderStatusHelper.class);
        this.executorService = mock(ScheduledExecutorService.class);

        this.scheduler = new WaterOrderDeliveryScheduler(this.statusUpdater, Optional.of(this.executorService),
                Optional.of(fixedClock));
    }

    @Test
    @DisplayName("addDeliverySchedule schedules taks correctly")
    public void testAddDeliverySchedule() throws DeliveryTaskNotFoundException {
        WaterOrder order = new WaterOrder("orderId1", "farmId1", LocalDateTime.of(2020, 1, 10, 10, 15),
                Duration.ofSeconds(10), WaterOrder.OrderStatus.REQUESTED);

        this.scheduler.addDeliverySchedule(order);

        verify(this.executorService).schedule(any(Runnable.class), eq(300L), eq(TimeUnit.SECONDS));
        verify(this.executorService).schedule(any(Runnable.class), eq(310L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("cancelDeliverySchedule schedules taks correctly")
    public void testCancelDeliverySchedule() throws DeliveryTaskNotFoundException, OrderNotFoundException {
        String orderId = "FakeOrderId";
        ScheduledFuture futureStart = mock(ScheduledFuture.class);
        ScheduledFuture futureEnd = mock(ScheduledFuture.class);
        when(this.executorService.schedule(any(Runnable.class), eq(300L), any(TimeUnit.class))).thenReturn(futureStart);
        when(this.executorService.schedule(any(Runnable.class), eq(310L), any(TimeUnit.class))).thenReturn(futureEnd);

        WaterOrder order = new WaterOrder(orderId, "farmId1", LocalDateTime.of(2020, 1, 10, 10, 15),
                Duration.ofSeconds(10), WaterOrder.OrderStatus.REQUESTED);

        this.scheduler.addDeliverySchedule(order);

        when(futureStart.isDone()).thenReturn(false);
        when(futureEnd.isDone()).thenReturn(false);

        this.scheduler.cancelDeliverySchdule(orderId);

        verify(futureStart).cancel(eq(true));
        verify(futureEnd).cancel(eq(true));
        verify(this.statusUpdater).updateOrderstatus(eq(orderId), eq(WaterOrder.OrderStatus.CANCELLED));
    }

    @Test
    @DisplayName("cancelDeliverySchedule throws DeliveryTaskNotFoundException if the task cannot be found.")
    public void testgetWaterOrderByOrderIdThrowsExceptionIfOrderIsNotThere() {
        assertThatThrownBy(() -> this.scheduler.cancelDeliverySchdule("unknownOrderId"))
                .isInstanceOf(DeliveryTaskNotFoundException.class).hasMessageContaining("Cannot find delivery task")
                .hasFieldOrPropertyWithValue("idName", "orderId");
    }
}