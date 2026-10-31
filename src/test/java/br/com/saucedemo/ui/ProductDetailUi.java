package br.com.saucedemo.ui;

import org.openqa.selenium.By;

public final class ProductDetailUi {

  public static final By CONTAINER = By.id("inventory_item_container");
  public static final By NAME = DataTest.of("inventory-item-name");
  public static final By PRICE = DataTest.of("inventory-item-price");
  public static final By ADD_TO_CART = DataTest.of("add-to-cart");
  public static final By REMOVE = DataTest.of("remove");
  public static final By BACK_TO_PRODUCTS = DataTest.of("back-to-products");

  private ProductDetailUi() {}
}
