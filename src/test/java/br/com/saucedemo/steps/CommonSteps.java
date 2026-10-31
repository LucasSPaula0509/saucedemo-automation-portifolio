package br.com.saucedemo.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.saucedemo.data.PageTitles;
import br.com.saucedemo.pages.ActionButtonsComponent;
import br.com.saucedemo.pages.FeedbackComponent;
import br.com.saucedemo.pages.HeaderComponent;
import br.com.saucedemo.pages.LoginPage;
import br.com.saucedemo.pages.MenuComponent;
import br.com.saucedemo.pages.ProductsPage;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

/** Passos usados por mais de uma funcionalidade. */
public class CommonSteps {

  private final FeedbackComponent feedback;
  private final HeaderComponent header;
  private final MenuComponent menu;
  private final ActionButtonsComponent actionButtons;
  private final LoginPage loginPage;
  private final ProductsPage productsPage;

  public CommonSteps(
      FeedbackComponent feedback,
      HeaderComponent header,
      MenuComponent menu,
      ActionButtonsComponent actionButtons,
      LoginPage loginPage,
      ProductsPage productsPage) {
    this.feedback = feedback;
    this.header = header;
    this.menu = menu;
    this.actionButtons = actionButtons;
    this.loginPage = loginPage;
    this.productsPage = productsPage;
  }

  @Quando("ele aciona {string}")
  public void pressButton(String label) {
    actionButtons.press(label);
  }

  @Quando("ele seleciona a opção {string} no menu lateral")
  public void selectSideMenuOption(String option) {
    menu.open();
    menu.select(option);
  }

  @Quando("ele seleciona a opção {string}")
  public void selectOpenMenuOption(String option) {
    menu.select(option);
  }

  @Então("a mensagem {string} é exibida")
  public void messageIsDisplayed(String message) {
    assertThat(feedback.message()).as("Mensagem exibida ao usuário").isEqualTo(message);
  }

  @Então("a página de produtos é exibida")
  public void productsPageIsDisplayed() {
    assertThat(productsPage.isLoaded()).as("Página de produtos carregada").isTrue();
    assertThat(header.pageTitle()).as("Título da página").isEqualTo(PageTitles.PRODUCTS);
  }

  @Então("a página de login é exibida")
  public void loginPageIsDisplayed() {
    assertThat(loginPage.isLoaded()).as("Página de login carregada").isTrue();
  }

  @Então("o contador do carrinho exibe {string}")
  public void cartBadgeShows(String count) {
    assertThat(header.cartBadgeText()).as("Contador do carrinho").isEqualTo(count);
  }

  @Então("o contador do carrinho não é exibido")
  public void cartBadgeIsNotDisplayed() {
    assertThat(header.isCartBadgeAbsent()).as("Contador do carrinho ausente").isTrue();
  }

  @Então("o botão do produto {string} exibe {string}")
  public void productButtonShows(String product, String label) {
    assertThat(productsPage.productButtonText(product))
        .as("Botão do produto '%s'", product)
        .isEqualTo(label);
  }
}
