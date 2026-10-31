package br.com.saucedemo.ui;

import org.openqa.selenium.By;

public final class CheckoutInfoUi {

  public static final By FIRST_NAME = DataTest.of("firstName");
  public static final By LAST_NAME = DataTest.of("lastName");
  public static final By POSTAL_CODE = DataTest.of("postalCode");
  public static final By CONTINUE = DataTest.of("continue");
  public static final By CANCEL = DataTest.of("cancel");

  private CheckoutInfoUi() {}
}
