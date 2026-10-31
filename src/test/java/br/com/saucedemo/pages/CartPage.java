package br.com.saucedemo.pages;

import static br.com.saucedemo.ui.CartUi.CART_LIST;
import static br.com.saucedemo.ui.CartUi.CHECKOUT;
import static br.com.saucedemo.ui.CartUi.ITEM_NAMES;
import static br.com.saucedemo.ui.CartUi.ITEM_PRICES;
import static br.com.saucedemo.ui.CartUi.ITEM_QUANTITIES;

import br.com.saucedemo.data.CartItemRow;
import br.com.saucedemo.data.Paths;
import br.com.saucedemo.pages.base.BasePage;
import br.com.saucedemo.ui.CartUi;
import java.util.List;
import java.util.stream.IntStream;

public class CartPage extends BasePage {

  public boolean isLoaded() {
    return isDisplayed(CART_LIST) && currentPath().equals(Paths.CART);
  }

  public List<CartItemRow> items() {
    List<String> quantities = textsOf(ITEM_QUANTITIES);
    List<String> names = textsOf(ITEM_NAMES);
    List<String> prices = textsOf(ITEM_PRICES);
    return IntStream.range(0, names.size())
        .mapToObj(i -> new CartItemRow(quantities.get(i), names.get(i), prices.get(i)))
        .toList();
  }

  public List<String> itemNames() {
    return textsOf(ITEM_NAMES);
  }

  public void remove(String productName) {
    click(CartUi.removeButton(productName));
  }

  public void checkout() {
    click(CHECKOUT);
  }
}
