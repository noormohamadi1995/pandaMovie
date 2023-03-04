package com.virlabs.demo_flx_application.services;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;


public class BillingSubs {
    String TAG = "BillingSubs";

    BillingClient billingClient;
    Activity activity;
    List<String> listSkuStore;
    CallBackBilling callBackBilling;
    CallBackPrice callBackPrice;
    CallBackCheck callBackCheck;

    public BillingSubs(Activity activity, List<String> listSkuStore, CallBackCheck callBackCheck) {
        this.activity = activity;
        this.listSkuStore = listSkuStore;
        this.callBackCheck = callBackCheck;

        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                    }
                }).build();

        checkPurchase();
    }

    public BillingSubs(Activity activity, List<String> listSkuStore, CallBackBilling callBackBilling) {
        this.activity = activity;
        this.listSkuStore = listSkuStore;
        this.callBackBilling = callBackBilling;

        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        Log.d(TAG, "onPurchasesUpdated: " + billingResult.getResponseCode());
                        // confirm purchased, otherwise refund money
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            Log.d(TAG, "onPurchasesUpdated: " + list);
                            for (Purchase purchase : list) {
                                // for non-consumables (buy one time)
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                                            AcknowledgePurchaseParams.newBuilder()
                                                    .setPurchaseToken(purchase.getPurchaseToken())
                                                    .build();
                                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                                        @Override
                                        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                            Log.d(TAG, "onAcknowledgePurchaseResponse: " + billingResult.getDebugMessage());
                                        }
                                    });

                                    callBackBilling.onPurchase();
                                    return;
                                }
                            }
                        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                            callBackBilling.onNotPurchase();
                        }
                    }
                }).build();
    }

    public BillingSubs (Activity activity, List<String> listSkuStore, CallBackPrice callBackPrice) {
        this.activity = activity;
        this.listSkuStore = listSkuStore;
        this.callBackPrice = callBackPrice;

        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                    }
                }).build();

        getPrice();
    }

    public void purchase(String sku) {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d(TAG, "onBillingSetupFinished: " );
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: OK" );
                    if (billingClient.isReady()) {
                        SkuDetailsParams skuDetailsParams = SkuDetailsParams
                                .newBuilder()
                                .setSkusList(listSkuStore)
                                .setType(BillingClient.SkuType.SUBS)
                                .build();

                        billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                                for (final SkuDetails skuDetails : list) {
                                    Log.d(TAG, "onSkuDetailsResponse: " + list);
                                    if (skuDetails.getSku().equals(sku)) {
                                        BillingFlowParams billingFlowParams = BillingFlowParams
                                                .newBuilder()
                                                .setSkuDetails(skuDetails)
                                                .build();

                                        billingClient.launchBillingFlow(activity, billingFlowParams);
                                    }
                                }
                            }
                        });
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                    callBackBilling.onNotLogin();
                    Log.d(TAG, "onBillingSetupFinished: onNotLogin" );
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    public void checkPurchase() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d(TAG, "onBillingSetupFinished: " );
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {


                    Log.d(TAG, "onBillingSetupFinished: OK" );

                    List<Purchase> listPurchase = new ArrayList<>();
                    billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build(), (billingResult1, list) -> {
                                checkSubs(list);

                            });
                    return;
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                    callBackCheck.onNotPurchase();
                    Log.d(TAG, "onBillingSetupFinished: onNotPurchase" );
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }
    public  void checkSubs(List<Purchase> listPurchase ){
        Log.d(TAG, "onBillingSetupFinished: list" +listPurchase.size());
        for (Purchase purchase : listPurchase) {
            Log.d(TAG, "onBillingSetupFinished: purchase "+purchase );
            for (String s : listSkuStore) {

                if (purchase.getSkus().contains(s.toLowerCase())) {
                    if( purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED ){
                        callBackCheck.onPurchase();
                    }
                    Log.d(TAG, "purchased: " + s);

                    return;
                }
            }
        }
        callBackCheck.onNotPurchase();
    }
    public void getPrice() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (billingClient.isReady()) {
                        SkuDetailsParams skuDetailsParams = SkuDetailsParams
                                .newBuilder()
                                .setSkusList(listSkuStore)
                                .setType(BillingClient.SkuType.SUBS)
                                .build();

                        billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                                Log.d(TAG, "BillingSubs getPrice: " + list);
                                List<Billing> listBilling = new ArrayList<>();
                                for (SkuDetails skuDetails : list) {
                                    String title = skuDetails.getTitle();
                                    if (skuDetails.getTitle().contains("(")) {
                                        title = skuDetails.getTitle().substring(0, skuDetails.getTitle().indexOf("(") - 1);
                                    }

                                    listBilling.add(new Billing(
                                            skuDetails.getSku(),
                                            title,
                                            skuDetails.getDescription(),
                                            skuDetails.getFreeTrialPeriod(),
                                            skuDetails.getPrice()));
                                }
                                callBackPrice.onPrice(listBilling);
                            }
                        });
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                    callBackPrice.onNotLogin();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                callBackPrice.onNotLogin();
            }
        });
    }
}
