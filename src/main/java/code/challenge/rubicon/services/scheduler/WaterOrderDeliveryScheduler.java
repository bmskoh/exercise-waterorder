package code.challenge.rubicon.services.scheduler;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.orderstatus.IWaterOrderStatusHelper;;

@Component
public class WaterOrderDeliveryScheduler implements IWaterOrderDeliveryScheduler {

    private Logger logger = LoggerFactory.getLogger(WaterOrderDeliveryScheduler.class);

    private IWaterOrderStatusHelper statusChanger;

    private ScheduledExecutorService scheduler;

    private Map<String, DeliveryTask> scheduledDeliveries = new HashMap<>();

    private Clock clock;

    public WaterOrderDeliveryScheduler(IWaterOrderStatusHelper statusChanger,
            Optional<ScheduledExecutorService> scheduler, Optional<Clock> clock) {
        this.statusChanger = statusChanger;
        if (scheduler.isPresent()) {
            this.scheduler = scheduler.get();
        } else {
            this.scheduler = Executors.newScheduledThreadPool(1);
        }
        if (clock.isPresent()) {
            this.clock = clock.get();
        } else {
            this.clock = Clock.systemDefaultZone();
        }
    }

    @Override
    public void addDeliverySchedule(WaterOrder waterOrder) {
        DeliveryTask deliveryTask = new DeliveryTask(waterOrder);
        deliveryTask.scheduleDelivery();
        this.scheduledDeliveries.put(waterOrder.getOrderId(), deliveryTask);
    }

    @Override
    public void cancelDeliverySchdule(String orderId) throws DeliveryTaskNotFoundException, OrderNotFoundException {
        DeliveryTask deliveryTask = this.scheduledDeliveries.get(orderId);
        if (deliveryTask == null) {
            throw new DeliveryTaskNotFoundException("orderId", "Cannot find delivery task for " + orderId);
        }
        deliveryTask.cancelDelivery();
    }

    private class DeliveryTask {
        private WaterOrder waterOrder;
        private ScheduledFuture<?> scheduledDeliverStart;
        private ScheduledFuture<?> scheduledDeliverEnd;

        DeliveryTask(WaterOrder waterOrder) {
            this.waterOrder = waterOrder;
        }

        void scheduleDelivery() {
            long delayToStartTime = LocalDateTime.now(WaterOrderDeliveryScheduler.this.clock)
                    .until(waterOrder.getStartDateTime(), ChronoUnit.SECONDS);

            this.scheduledDeliverStart = WaterOrderDeliveryScheduler.this.scheduler.schedule(new Runnable() {
                public void run() {
                    logger.info("Delivery started. " + LocalDateTime.now());
                    try {
                        WaterOrderDeliveryScheduler.this.statusChanger.updateOrderstatus(
                                DeliveryTask.this.waterOrder.getOrderId(), WaterOrder.OrderStatus.IN_PROGRESS);
                    } catch (OrderNotFoundException ex) {
                        // This should not happen. Log it.
                        logger.error("Connot find order so failed to update the status to IN_PROGRESS. orderId: "
                                + DeliveryTask.this.waterOrder.getOrderId());
                    }
                }
            }, delayToStartTime, TimeUnit.SECONDS);
            this.scheduledDeliverEnd = WaterOrderDeliveryScheduler.this.scheduler.schedule(new Runnable() {
                public void run() {
                    logger.info("Delivery finished. " + LocalDateTime.now());
                    WaterOrderDeliveryScheduler.this.scheduledDeliveries
                            .remove(DeliveryTask.this.waterOrder.getOrderId());
                    try {
                        WaterOrderDeliveryScheduler.this.statusChanger.updateOrderstatus(
                                DeliveryTask.this.waterOrder.getOrderId(), WaterOrder.OrderStatus.DELIVERED);
                    } catch (OrderNotFoundException ex) {
                        // Same as above. This shouldn't happen. Log it.
                        logger.error("Connot find order so failed to update the status to DELIVERED. orderId: "
                                + DeliveryTask.this.waterOrder.getOrderId());
                    }
                }
            }, delayToStartTime + this.waterOrder.getDuration().getSeconds(), TimeUnit.SECONDS);
        }

        void cancelDelivery() throws OrderNotFoundException {
            if (!this.scheduledDeliverStart.isDone()) {
                this.scheduledDeliverStart.cancel(true);
            }
            if (!this.scheduledDeliverEnd.isDone()) {
                this.scheduledDeliverEnd.cancel(true);
            }
            WaterOrderDeliveryScheduler.this.statusChanger.updateOrderstatus(this.waterOrder.getOrderId(),
                    WaterOrder.OrderStatus.CANCELLED);
        }
    }
}