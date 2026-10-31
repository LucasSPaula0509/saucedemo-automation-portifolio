package br.com.saucedemo.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.saucedemo.data.Customer;
import br.com.saucedemo.data.PageTitles;
import br.com.saucedemo.data.Paths;
import br.com.saucedemo.data.Products;
import br.com.saucedemo.pages.ActionButtonsComponent;
import br.com.saucedemo.pages.BrowserNavigation;
import br.com.saucedemo.pages.CartPage;
import br.com.saucedemo.pages.CheckoutInfoPage;
import br.com.saucedemo.pages.CheckoutOverviewPage;
import br.com.saucedemo.pages.HeaderComponent;
import br.com.saucedemo.support.Money;
import br.com.saucedemo.support.OrderTotals;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.qameta.allure.Allure;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;

public class CheckoutSteps {

  private static final List<String> DEFAULT_PRODUCTS = List.of(Products.BACKPACK);
  private static final String FIELD_COLUMN = "campo";
  private static final String VALUE_COLUMN = "valor";

  private final ShopperJourney journey;
  private final HeaderComponent header;
  private final ActionButtonsComponent actionButtons;
  private final CartPage cartPage;
  private final CheckoutInfoPage checkoutInfoPage;
  private final CheckoutOverviewPage overviewPage;
  private final BrowserNavigation browser;

  public CheckoutSteps(
      ShopperJourney journey,
      HeaderComponent header,
      ActionButtonsComponent actionButtons,
      CartPage cartPage,
      CheckoutInfoPage checkoutInfoPage,
      CheckoutOverviewPage overviewPage,
      BrowserNavigation browser) {
    this.journey = journey;
    this.header = header;
    this.actionButtons = actionButtons;
    this.cartPage = cartPage;
    this.checkoutInfoPage = checkoutInfoPage;
    this.overviewPage = overviewPage;
    this.browser = browser;
  }

  @Dado("que o usuário {string} está no formulário de informações do checkout")
  public void userIsOnCheckoutInformation(String username) {
    journey.atCheckoutInformation(username, DEFAULT_PRODUCTS);
  }

  @Dado("que o usuário {string} preencheu os dados do checkout com o produto {string} no carrinho")
  public void userFilledCheckoutWithProduct(String username, String product) {
    journey.withCheckoutInformationFilled(username, List.of(product));
  }

  @Dado(
      "que o usuário {string} preencheu os dados do checkout com os produtos {string} no carrinho")
  public void userFilledCheckoutWithProducts(String username, String products) {
    journey.withCheckoutInformationFilled(username, splitProducts(products));
  }

  @Dado("que o usuário {string} está no resumo do pedido")
  public void userIsOnOverview(String username) {
    journey.atCheckoutOverview(username, DEFAULT_PRODUCTS);
  }

  @Dado("que o usuário {string} está no resumo do pedido com o produto {string}")
  public void userIsOnOverviewWithProduct(String username, String product) {
    journey.atCheckoutOverview(username, List.of(product));
  }

  @Dado("que o usuário {string} está na página {string}")
  public void userIsOnCheckoutPage(String username, String pageTitle) {
    journey.atCheckoutOverview(username, DEFAULT_PRODUCTS);
    overviewPage.finish();
    assertThat(header.pageTitle()).as("Pré-condição: página atual").isEqualTo(pageTitle);
  }

  @Dado("que o usuário {string} está no carrinho vazio")
  public void userIsOnEmptyCart(String username) {
    journey.loggedInWithEmptyCart(username);
    journey.openCart();
  }

  @Quando("ele aciona {string} no carrinho")
  public void pressesButtonOnCart(String label) {
    journey.openCart();
    actionButtons.press(label);
  }

  @Quando("ele continua informando nome {string}, sobrenome {string} e CEP {string}")
  public void continuesWithCustomerData(String firstName, String lastName, String postalCode) {
    checkoutInfoPage.submit(new Customer(firstName, lastName, postalCode));
  }

  @Quando("ele visualiza o resumo do pedido")
  public void viewsOrderOverview() {
    checkoutInfoPage.proceed();
  }

  @Então("o formulário {string} é exibido")
  public void checkoutFormIsDisplayed(String title) {
    assertThat(checkoutInfoPage.isLoaded()).as("Formulário do checkout carregado").isTrue();
    assertThat(header.pageTitle()).as("Título da página").isEqualTo(title);
  }

  @Então("o resumo {string} é exibido")
  public void overviewIsDisplayed(String title) {
    assertThat(overviewPage.isLoaded()).as("Resumo do pedido carregado").isTrue();
    assertThat(header.pageTitle()).as("Título da página").isEqualTo(title);
  }

  @Então("o resumo exibe as informações")
  public void overviewShowsInformation(DataTable expected) {
    Map<String, String> actual = overviewPage.summaryInfo();
    SoftAssertions.assertSoftly(
        softly ->
            expected
                .asMaps()
                .forEach(
                    row ->
                        softly
                            .assertThat(actual.get(row.get(FIELD_COLUMN)))
                            .as("Resumo - %s", row.get(FIELD_COLUMN))
                            .isEqualTo(row.get(VALUE_COLUMN))));
  }

  @Então("o resumo exibe {string}, {string} e {string}")
  public void overviewShowsTotals(String subtotal, String tax, String total) {
    OrderTotals expectedByRule = OrderTotals.fromItemPrices(overviewPage.itemPrices());
    Allure.addAttachment("Cálculo do pedido", "text/plain", expectedByRule.describe());

    SoftAssertions.assertSoftly(
        softly -> {
          softly.assertThat(overviewPage.subtotal()).as("Subtotal exibido").isEqualTo(subtotal);
          softly.assertThat(overviewPage.tax()).as("Imposto exibido").isEqualTo(tax);
          softly.assertThat(overviewPage.total()).as("Total exibido").isEqualTo(total);
          softly
              .assertThat(Money.parse(overviewPage.subtotal()))
              .as("Subtotal pela regra")
              .isEqualByComparingTo(expectedByRule.subtotal());
          softly
              .assertThat(Money.parse(overviewPage.tax()))
              .as("Imposto pela regra de 8%")
              .isEqualByComparingTo(expectedByRule.tax());
          softly
              .assertThat(Money.parse(overviewPage.total()))
              .as("Total pela regra")
              .isEqualByComparingTo(expectedByRule.total());
        });
  }

  @Então("o carrinho é exibido")
  public void cartIsDisplayed() {
    assertThat(cartPage.isLoaded()).as("Carrinho carregado").isTrue();
    assertThat(header.pageTitle()).as("Título da página").isEqualTo(PageTitles.CART);
  }

  @Então("o checkout não é iniciado")
  public void checkoutIsNotStarted() {
    assertThat(browser.path())
        .as("Página após acionar Checkout com o carrinho vazio")
        .isNotEqualTo(Paths.CHECKOUT_STEP_ONE);
  }

  private static List<String> splitProducts(String products) {
    return Arrays.stream(products.split(",")).map(String::trim).toList();
  }
}
