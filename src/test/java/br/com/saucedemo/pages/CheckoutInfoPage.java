package br.com.saucedemo.pages;

import static br.com.saucedemo.ui.CheckoutInfoUi.CONTINUE;
import static br.com.saucedemo.ui.CheckoutInfoUi.FIRST_NAME;
import static br.com.saucedemo.ui.CheckoutInfoUi.LAST_NAME;
import static br.com.saucedemo.ui.CheckoutInfoUi.POSTAL_CODE;

import br.com.saucedemo.data.Customer;
import br.com.saucedemo.data.Paths;
import br.com.saucedemo.pages.base.BasePage;

public class CheckoutInfoPage extends BasePage {

  public boolean isLoaded() {
    return isDisplayed(FIRST_NAME) && currentPath().equals(Paths.CHECKOUT_STEP_ONE);
  }

  public void fill(Customer customer) {
    type(FIRST_NAME, customer.firstName());
    type(LAST_NAME, customer.lastName());
    type(POSTAL_CODE, customer.postalCode());
  }

  public void proceed() {
    click(CONTINUE);
  }

  public void submit(Customer customer) {
    fill(customer);
    proceed();
  }
}
