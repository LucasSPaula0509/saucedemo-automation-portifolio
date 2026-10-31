package br.com.saucedemo.ui;

import br.com.saucedemo.support.SlugUtils;
import org.openqa.selenium.By;

public final class ProductsUi {

  public static final By INVENTORY_LIST = DataTest.of("inventory-list");
  public static final By ITEM_NAMES = DataTest.of("inventory-item-name");
  public static final By ITEM_PRICES = DataTest.of("inventory-item-price");
  public static final By SORT_SELECT = DataTest.of("product-sort-container");
  public static final By ACTIVE_SORT = DataTest.of("active-option");

  private ProductsUi() {}

  public static By addToCartButton(String productName) {
    return DataTest.of("add-to-cart-" + SlugUtils.toSlug(productName));
  }

  public static By removeButton(String productName) {
    return DataTest.of("remove-" + SlugUtils.toSlug(productName));
  }

  /** Botão do produto, esteja ele no estado "Add to cart" ou "Remove". */
  public static By productButton(String productName) {
    return By.cssSelector("button[data-test$='-%s']".formatted(SlugUtils.toSlug(productName)));
  }

  public static By productTitle(String productName) {
    return By.xpath(
        "//div[@data-test='inventory-item-name' and normalize-space()='%s']"
            .formatted(productName));
  }
}
