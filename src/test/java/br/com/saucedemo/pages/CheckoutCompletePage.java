package br.com.saucedemo.pages;

import static br.com.saucedemo.ui.CheckoutCompleteUi.COMPLETE_HEADER;

import br.com.saucedemo.data.Paths;
import br.com.saucedemo.pages.base.BasePage;

public class CheckoutCompletePage extends BasePage {

  public boolean isLoaded() {
    return isDisplayed(COMPLETE_HEADER) && currentPath().equals(Paths.CHECKOUT_COMPLETE);
  }

  public String confirmationMessage() {
    return textOf(COMPLETE_HEADER);
  }
}
