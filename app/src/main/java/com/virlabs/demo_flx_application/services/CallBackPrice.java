package com.virlabs.demo_flx_application.services;

import java.util.List;

public interface CallBackPrice {
    void onNotLogin();
    void onPrice(List<Billing> listBilling);
}