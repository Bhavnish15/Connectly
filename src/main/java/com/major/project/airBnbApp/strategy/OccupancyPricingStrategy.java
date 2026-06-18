package com.major.project.airBnbApp.strategy;

import com.major.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        double occupencyRate = (double) inventory.getBookedCount() / inventory.getTotalCount();
        return price;
     }
}
