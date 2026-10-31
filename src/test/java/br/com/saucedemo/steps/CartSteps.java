package br.com.saucedemo.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.saucedemo.data.CartItemRow;
import br.com.saucedemo.pages.BrowserNavigation;
import br.com.saucedemo.pages.CartPage;
import br.com.saucedemo.pages.HeaderComponent;
import br.com.saucedemo.pages.ProductDetailPage;
import br.com.saucedemo.pages.ProductsPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import java.util.List;

public class CartSteps {

  private static final String NAME_COLUMN = "produto";

  private final ShopperJourney journey;
  private final ProductsPage productsPage;
  private final ProductDetailPage detailPage;
  private final CartPage cartPage;
  private final HeaderComponent header;
  private final BrowserNavigation browser;

  public CartSteps(
      ShopperJourney journey,
      ProductsPage productsPage,
      ProductDetailPage detailPage,
      CartPage cartPage,
      HeaderComponent header,
      BrowserNavigation browser) {
    this.journey = journey;
    this.productsPage = productsPage;
    this.detailPage = detailPage;
    this.cartPage = cartPage;
    this.header = header;
    this.browser = browser;
  }

  @Dado("que o usuário {string} está na página de produtos com o carrinho vazio")
  public void userIsOnProductsWithEmptyCart(String username) {
    journey.loggedInWithEmptyCart(username);
  }

  @Dado("que o carrinho do usuário {string} contém os produtos {string} e {string}")
  public void cartContainsTwoProducts(String username, String first, String second) {
    journey.withCart(username, List.of(first, second));
  }

  @Dado("que o carrinho do usuário {string} contém apenas o produto {string}")
  public void cartContainsOnlyProduct(String username, String product) {
    journey.withCart(username, List.of(product));
  }

  @Dado("que o carrinho do usuário {string} contém o produto {string}")
  public void cartContainsProduct(String username, String product) {
    journey.withCart(username, List.of(product));
  }

  @Dado("que o usuário {string} está no detalhe do produto {string} com o carrinho vazio")
  public void userIsOnDetailWithEmptyCart(String username, String product) {
    journey.loggedInWithEmptyCart(username);
    productsPage.openDetail(product);
  }

  @Dado("que o usuário {string} adicionou o produto {string} pela página de detalhe")
  public void userAddedProductFromDetail(String username, String product) {
    journey.loggedInWithEmptyCart(username);
    productsPage.openDetail(product);
    detailPage.addToCart();
  }

  @Dado("que o usuário {string} adicionou os produtos {string} e {string}")
  public void userAddedTwoProducts(String username, String first, String second) {
    journey.withCart(username, List.of(first, second));
  }

  @Dado("que o usuário {string} está no carrinho")
  public void userIsOnCart(String username) {
    journey.loggedInOnProducts(username);
    journey.openCart();
  }

  @Quando("ele adiciona o produto {string} ao carrinho")
  public void addsProductToCart(String product) {
    productsPage.addToCart(product);
  }

  @Quando("ele adiciona ao carrinho os produtos")
  public void addsProductsToCart(DataTable products) {
    productsPage.addToCart(products.asMaps().stream().map(row -> row.get(NAME_COLUMN)).toList());
  }

  @Quando("ele adiciona o produto ao carrinho")
  public void addsCurrentProductToCart() {
    detailPage.addToCart();
  }

  @Quando("ele remove o produto {string} pela lista de produtos")
  public void removesProductFromList(String product) {
    productsPage.removeFromCart(product);
  }

  @Quando("ele volta para a lista de produtos")
  public void goesBackToProductList() {
    detailPage.backToProducts();
  }

  @Quando("ele acessa o carrinho")
  public void opensCart() {
    header.openCart();
  }

  @Quando("ele remove o produto {string} no carrinho")
  public void removesProductInCart(String product) {
    journey.openCart();
    cartPage.remove(product);
  }

  @Quando("ele recarrega a página de produtos")
  public void reloadsProductsPage() {
    browser.refresh();
  }

  @Então("o carrinho exibe os itens")
  public void cartShowsItems(List<CartItemRow> expected) {
    assertThat(cartPage.items()).as("Itens do carrinho").containsExactlyElementsOf(expected);
  }

  @Então("o carrinho exibe apenas o produto {string}")
  public void cartShowsOnlyProduct(String product) {
    assertThat(cartPage.itemNames()).as("Produtos no carrinho").containsExactly(product);
  }
}
