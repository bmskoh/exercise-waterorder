package code.challenge.rubicon.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class WaterOrder {

    public enum OrderStatus {
        REQUESTED, IN_PROGRESS, DELIVERED, CANCELLED
    }

    // A unique ID for identifying a farm
    @NotBlank(message = "Farm ID is required")
    private String farmId;

    // The date and time when water should be delivered
    @NotNull(message = "Start time is required")
    @Future
    private LocalDateTime startDateTime;

    // The duration of the order (e.g. Duration of 3 hours means water will flow
    // into the farm for 3 hours from the start date time).
    @NotNull(message = "Duration is required")
    private Duration duration;

    // Unique ID of this order
    private String orderId;
    // This order's current status
    private WaterOrder.OrderStatus status;

    public WaterOrder() {
    }

    public WaterOrder(String orderId, String farmId, LocalDateTime startDateTime, Duration duration,
            WaterOrder.OrderStatus status) {
        this.orderId = orderId;
        this.farmId = farmId;
        this.startDateTime = startDateTime;
        this.duration = duration;
        this.setStatus(status);
    }

    public void setStatus(WaterOrder.OrderStatus status) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String localTimeStr = timeFormatter.format(LocalDateTime.now());

        if (this.status == null || !this.status.equals(status)) {
            this.status = status;

            switch (status) {
            case REQUESTED:
                System.out.println(String.format("== %s == New water order is placed. orderId: %s, farmId: %s",
                        localTimeStr, this.orderId, this.farmId));
                break;
            case IN_PROGRESS:
                System.out.println(String.format("== %s == Water order starts. orderId: %s, farmId: %s", localTimeStr,
                        this.orderId, this.farmId));
                break;
            case DELIVERED:
                System.out.println(String.format("== %s == Water order is delivered. orderId: %s, farmId: %s",
                        localTimeStr, this.orderId, this.farmId));
                break;
            case CANCELLED:
                System.out.println(String.format("== %s == Water order is cancelled. orderId: %s, farmId: %s",
                        localTimeStr, this.orderId, this.farmId));
                break;
            }
        }
    }

    /**
     * This method returns custom message according to current order's status.
     *
     * @return Message representing current order'status
     */
    public String getStatusMessage() {
        switch (this.status) {
        case REQUESTED:
            return "Order has been placed but not yet delivered.";
        case IN_PROGRESS:
            return "Order is being delivered right now.";
        case DELIVERED:
            return "Order has been delieverd.";
        case CANCELLED:
            return "Order was cancelled before delivery.";
        }
        return "Unknown Status";
    }

    // getters
    public String getOrderId() {
        return this.orderId;
    }

    public String getFarmId() {
        return this.farmId;
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public WaterOrder.OrderStatus getStatus() {
        return this.status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof WaterOrder)) {
            return false;
        }
        WaterOrder waterOrder = (WaterOrder) o;
        return Objects.equals(orderId, waterOrder.orderId) && Objects.equals(farmId, waterOrder.farmId)
                && Objects.equals(startDateTime, waterOrder.startDateTime)
                && Objects.equals(duration, waterOrder.duration) && Objects.equals(status, waterOrder.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, farmId, startDateTime, duration, status);
    }

    @Override
    public String toString() {
        return "{" + " orderId='" + getOrderId() + "'" + ", farmId='" + getFarmId() + "'" + ", startDateTime='"
                + getStartDateTime() + "'" + ", duration='" + getDuration() + "'" + ", status='" + getStatus() + "'"
                + "}";
    }

}