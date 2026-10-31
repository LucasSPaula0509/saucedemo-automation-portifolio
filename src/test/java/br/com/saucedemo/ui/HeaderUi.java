package br.com.saucedemo.ui;

import org.openqa.selenium.By;

public final class HeaderUi {

  public static final By MENU_BUTTON = By.id("react-burger-menu-btn");
  public static final By CART_LINK = DataTest.of("shopping-cart-link");
  public static final By CART_BADGE = DataTest.of("shopping-cart-badge");
  public static final By PAGE_TITLE = DataTest.of("title");

  private HeaderUi() {}
}
