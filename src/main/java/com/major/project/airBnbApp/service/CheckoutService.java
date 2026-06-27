package com.major.project.airBnbApp.service;


import com.major.project.airBnbApp.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);

}
