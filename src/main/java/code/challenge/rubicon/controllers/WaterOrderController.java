package code.challenge.rubicon.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import code.challenge.rubicon.exceptions.DeliveryTaskNotFoundException;
import code.challenge.rubicon.exceptions.OrderNotFoundException;
import code.challenge.rubicon.exceptions.OrderValidityException;
import code.challenge.rubicon.model.WaterOrder;
import code.challenge.rubicon.services.IWaterOrderService;

/**
 * Rest controller that provides access to IWaterOrderService.
 */
@RestController
public class WaterOrderController {
    private IWaterOrderService waterOrderService;

    public WaterOrderController(IWaterOrderService waterOrderService) {
        this.waterOrderService = waterOrderService;
    }

    @GetMapping("/waterorders")
    public List<WaterOrder> getWaterOrders(@RequestParam(required = false) String farmid)
            throws OrderNotFoundException {
        // If farmid is given, search for the order with matching farmid.
        // Otherwise return all orders.
        if (farmid != null) {
            return waterOrderService.getWaterOrderByFarmrId(farmid);
        } else {
            return waterOrderService.getAllOrders();
        }
    }

    @GetMapping("/waterorders/{orderId}")
    public WaterOrder getWaterOrder(@PathVariable String orderId) throws OrderNotFoundException {
        return this.waterOrderService.getWaterOrderByOrderId(orderId);
    }

    @PostMapping("/waterorders")
    public WaterOrder addWaterOrder(@RequestBody @Valid WaterOrder newOrder) throws OrderValidityException {
        return this.waterOrderService.addWaterOrder(newOrder);
    }

    @PutMapping("/waterorders/{orderId}/cancellation")
    public void cancelWaterOrder(@PathVariable String orderId)
            throws OrderNotFoundException, OrderValidityException, DeliveryTaskNotFoundException {
        this.waterOrderService.cancelWaterOrder(orderId);
    }

    // =========================================================
    // Exception Handlers
    // =========================================================

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(OrderNotFoundException.class)
    public Map<String, String> handleOrderNotFoundException(OrderNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ex.getIdName(), ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OrderValidityException.class)
    public Map<String, String> handleOrderValidityException(OrderValidityException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ex.getAction().toString(), ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DeliveryTaskNotFoundException.class)
    public Map<String, String> handleDeliveryTaskNotFoundException(DeliveryTaskNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ex.getIdName().toString(), ex.getMessage());
        return error;
    }

    // =========================================================
    // End of Exception Handlers
    // =========================================================

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(new DurationValidator());
    }

    /**
     * Extra validator to filter negative duration. All other validations are done
     * by Hibernate validator by annotations in WaterOrder.java
     */
    class DurationValidator implements Validator {

        public boolean supports(Class clazz) {
            return WaterOrder.class.equals(clazz);
        }

        public void validate(Object obj, Errors e) {
            WaterOrder waterOrder = (WaterOrder) obj;
            if (waterOrder.getDuration() != null && waterOrder.getDuration().isNegative()) {
                e.rejectValue("duration", "Duration cannot be negative.");
            }
        }
    }
}