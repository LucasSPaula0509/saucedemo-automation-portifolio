package br.com.saucedemo.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.saucedemo.context.ScenarioContext;
import br.com.saucedemo.data.Customer;
import br.com.saucedemo.pages.CartPage;
import br.com.saucedemo.pages.CheckoutInfoPage;
import br.com.saucedemo.pages.HeaderComponent;
import br.com.saucedemo.pages.LoginPage;
import br.com.saucedemo.pages.ProductsPage;
import java.util.List;

/**
 * Pré-condições reutilizáveis montadas pela UI, sempre a partir de um navegador novo (carrinho
 * vazio). Cada etapa confirma que chegou onde deveria, para que uma falha de pré-condição não seja
 * confundida com falha do comportamento sob teste.
 */
public class ShopperJourney {

  private final ScenarioContext context;
  private final LoginPage loginPage;
  private final ProductsPage productsPage;
  private final HeaderComponent header;
  private final CartPage cartPage;
  private final CheckoutInfoPage checkoutInfoPage;

  public ShopperJourney(
      ScenarioContext context,
      LoginPage loginPage,
      ProductsPage productsPage,
      HeaderComponent header,
      CartPage cartPage,
      CheckoutInfoPage checkoutInfoPage) {
    this.context = context;
    this.loginPage = loginPage;
    this.productsPage = productsPage;
    this.header = header;
    this.cartPage = cartPage;
    this.checkoutInfoPage = checkoutInfoPage;
  }

  public void loggedInOnProducts(String username) {
    context.setUsername(username);
    loginPage.loginAs(username);
    assertThat(productsPage.isLoaded())
        .as("Pré-condição: página de produtos após login de '%s'", username)
        .isTrue();
  }

  public void loggedInWithEmptyCart(String username) {
    loggedInOnProducts(username);
    assertThat(header.isCartBadgeAbsent()).as("Pré-condição: carrinho vazio").isTrue();
  }

  public void withCart(String username, List<String> products) {
    loggedInWithEmptyCart(username);
    productsPage.addToCart(products);
    assertThat(header.cartBadgeText())
        .as("Pré-condição: contador do carrinho com %s", products)
        .isEqualTo(String.valueOf(products.size()));
  }

  public void atCart(String username, List<String> products) {
    withCart(username, products);
    openCart();
  }

  public void openCart() {
    header.openCart();
    assertThat(cartPage.isLoaded()).as("Pré-condição: carrinho aberto").isTrue();
  }

  public void atCheckoutInformation(String username, List<String> products) {
    atCart(username, products);
    cartPage.checkout();
    assertThat(checkoutInfoPage.isLoaded()).as("Pré-condição: formulário do checkout").isTrue();
  }

  public void withCheckoutInformationFilled(String username, List<String> products) {
    atCheckoutInformation(username, products);
    checkoutInfoPage.fill(Customer.DEFAULT);
  }

  public void atCheckoutOverview(String username, List<String> products) {
    withCheckoutInformationFilled(username, products);
    checkoutInfoPage.proceed();
  }
}
