package br.com.saucedemo.ui;

import br.com.saucedemo.support.SlugUtils;
import org.openqa.selenium.By;

public final class CartUi {

  public static final By CART_LIST = DataTest.of("cart-list");
  public static final By ITEM_QUANTITIES = DataTest.of("item-quantity");
  public static final By ITEM_NAMES = DataTest.of("inventory-item-name");
  public static final By ITEM_PRICES = DataTest.of("inventory-item-price");
  public static final By CONTINUE_SHOPPING = DataTest.of("continue-shopping");
  public static final By CHECKOUT = DataTest.of("checkout");

  private CartUi() {}

  public static By removeButton(String productName) {
    return DataTest.of("remove-" + SlugUtils.toSlug(productName));
  }
}
