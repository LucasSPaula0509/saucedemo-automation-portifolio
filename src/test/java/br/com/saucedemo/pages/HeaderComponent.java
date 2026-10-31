package br.com.saucedemo.pages;

import static br.com.saucedemo.ui.HeaderUi.CART_BADGE;
import static br.com.saucedemo.ui.HeaderUi.CART_LINK;
import static br.com.saucedemo.ui.HeaderUi.PAGE_TITLE;

import br.com.saucedemo.pages.base.BasePage;

public class HeaderComponent extends BasePage {

  public String cartBadgeText() {
    return textOf(CART_BADGE);
  }

  public boolean isCartBadgeAbsent() {
    return isAbsent(CART_BADGE);
  }

  public void openCart() {
    click(CART_LINK);
  }

  public String pageTitle() {
    return textOf(PAGE_TITLE);
  }
}
