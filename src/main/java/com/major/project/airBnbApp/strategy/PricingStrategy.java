package com.major.project.airBnbApp.strategy;

import com.major.project.airBnbApp.entity.Inventory;
import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
