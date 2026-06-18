package com.major.project.airBnbApp.service;

import com.major.project.airBnbApp.entity.Hotel;
import com.major.project.airBnbApp.entity.HotelMinPrice;
import com.major.project.airBnbApp.entity.Inventory;
import com.major.project.airBnbApp.repository.HotelMinPriceRepository;
import com.major.project.airBnbApp.repository.HotelRepository;
import com.major.project.airBnbApp.repository.InventoryRepository;
import com.major.project.airBnbApp.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

    // Schedular to update the inventory and HotelMinPrice tables every hour

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

//    @Scheduled(cron = "*/5 * * * * *")
    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {
        int page = 0;
        int batchSize = 100;

        while(true){
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if(hotelPage.isEmpty()){
                break;
            }
            hotelPage.getContent().forEach(hotel -> updateHotelPrices(hotel));

            page++;
        }
    }

    private void updateHotelPrices(Hotel hotel){
        log.info("Updating hotel Prices");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);
        updateInventoryPrices(inventoryList);
        updateHotelMinPrice(hotel, inventoryList, startDate, endDate);

    }
    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate){
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));
        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelMinPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date).orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setPrice(price);
            hotelMinPrices.add(hotelMinPrice);
        });
        // Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelMinPrices);
    }
    private void updateInventoryPrices(List<Inventory> inventoryList){
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }
}























