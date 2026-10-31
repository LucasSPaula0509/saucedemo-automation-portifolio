package br.com.saucedemo.pages;

import static br.com.saucedemo.ui.ProductDetailUi.ADD_TO_CART;
import static br.com.saucedemo.ui.ProductDetailUi.BACK_TO_PRODUCTS;
import static br.com.saucedemo.ui.ProductDetailUi.CONTAINER;
import static br.com.saucedemo.ui.ProductDetailUi.NAME;
import static br.com.saucedemo.ui.ProductDetailUi.PRICE;

import br.com.saucedemo.data.Paths;
import br.com.saucedemo.pages.base.BasePage;

public class ProductDetailPage extends BasePage {

  public boolean isLoaded() {
    return isDisplayed(CONTAINER) && currentPath().equals(Paths.PRODUCT_DETAIL);
  }

  public String name() {
    return textOf(NAME);
  }

  public String price() {
    return textOf(PRICE);
  }

  public void addToCart() {
    click(ADD_TO_CART);
  }

  public void backToProducts() {
    click(BACK_TO_PRODUCTS);
  }
}
