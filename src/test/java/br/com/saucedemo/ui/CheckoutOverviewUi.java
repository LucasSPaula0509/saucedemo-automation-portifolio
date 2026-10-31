package br.com.saucedemo.ui;

import org.openqa.selenium.By;

public final class CheckoutOverviewUi {

  public static final By SUMMARY_CONTAINER = DataTest.of("checkout-summary-container");
  public static final By ITEM_PRICES = DataTest.of("inventory-item-price");
  public static final By PAYMENT_INFO_LABEL = DataTest.of("payment-info-label");
  public static final By PAYMENT_INFO_VALUE = DataTest.of("payment-info-value");
  public static final By SHIPPING_INFO_LABEL = DataTest.of("shipping-info-label");
  public static final By SHIPPING_INFO_VALUE = DataTest.of("shipping-info-value");
  public static final By SUBTOTAL = DataTest.of("subtotal-label");
  public static final By TAX = DataTest.of("tax-label");
  public static final By TOTAL = DataTest.of("total-label");
  public static final By FINISH = DataTest.of("finish");
  public static final By CANCEL = DataTest.of("cancel");

  private CheckoutOverviewUi() {}
}
