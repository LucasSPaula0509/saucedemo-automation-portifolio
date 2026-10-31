package br.com.saucedemo.pages;

import static br.com.saucedemo.ui.ProductsUi.ACTIVE_SORT;
import static br.com.saucedemo.ui.ProductsUi.INVENTORY_LIST;
import static br.com.saucedemo.ui.ProductsUi.ITEM_NAMES;
import static br.com.saucedemo.ui.ProductsUi.ITEM_PRICES;
import static br.com.saucedemo.ui.ProductsUi.SORT_SELECT;

import br.com.saucedemo.data.Paths;
import br.com.saucedemo.data.ProductRow;
import br.com.saucedemo.pages.base.BasePage;
import br.com.saucedemo.ui.ProductsUi;
import java.util.List;
import java.util.stream.IntStream;

public class ProductsPage extends BasePage {

  public void open() {
    open(Paths.INVENTORY);
  }

  public boolean isLoaded() {
    return isDisplayed(INVENTORY_LIST) && currentPath().equals(Paths.INVENTORY);
  }

  public List<ProductRow> products() {
    List<String> names = textsOf(ITEM_NAMES);
    List<String> prices = textsOf(ITEM_PRICES);
    return IntStream.range(0, names.size())
        .mapToObj(index -> new ProductRow(names.get(index), prices.get(index)))
        .toList();
  }

  public List<String> productNames() {
    return textsOf(ITEM_NAMES);
  }

  public void sortBy(String option) {
    selectByVisibleText(SORT_SELECT, option);
  }

  public String activeSort() {
    return textOf(ACTIVE_SORT);
  }

  public void addToCart(String productName) {
    click(ProductsUi.addToCartButton(productName));
  }

  public void addToCart(List<String> productNames) {
    productNames.forEach(this::addToCart);
  }

  public void removeFromCart(String productName) {
    click(ProductsUi.removeButton(productName));
  }

  public String productButtonText(String productName) {
    return textOf(ProductsUi.productButton(productName));
  }

  public void openDetail(String productName) {
    click(ProductsUi.productTitle(productName));
  }
}
