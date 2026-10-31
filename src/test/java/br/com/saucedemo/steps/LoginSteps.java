package br.com.saucedemo.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.saucedemo.context.ScenarioContext;
import br.com.saucedemo.data.MenuOptions;
import br.com.saucedemo.pages.BrowserNavigation;
import br.com.saucedemo.pages.LoginPage;
import br.com.saucedemo.pages.MenuComponent;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class LoginSteps {

  private static final String MASKED_INPUT_TYPE = "password";

  private final ScenarioContext context;
  private final ShopperJourney journey;
  private final LoginPage loginPage;
  private final MenuComponent menu;
  private final BrowserNavigation browser;

  public LoginSteps(
      ScenarioContext context,
      ShopperJourney journey,
      LoginPage loginPage,
      MenuComponent menu,
      BrowserNavigation browser) {
    this.context = context;
    this.journey = journey;
    this.loginPage = loginPage;
    this.menu = menu;
    this.browser = browser;
  }

  @Dado("que o usuário {string} está na página de login")
  public void userIsOnLoginPage(String username) {
    context.setUsername(username);
    loginPage.open();
  }

  @Dado("que o visitante está na página de login")
  public void visitorIsOnLoginPage() {
    loginPage.open();
  }

  /** Cada cenário inicia com um navegador novo, em modo anônimo e sem sessão. */
  @Dado("que o visitante não está autenticado")
  public void visitorIsNotAuthenticated() {}

  @Dado("que o usuário {string} está autenticado")
  public void userIsAuthenticated(String username) {
    journey.loggedInOnProducts(username);
  }

  @Dado("que o usuário {string} encerrou a sessão pelo menu lateral")
  public void userHasLoggedOut(String username) {
    journey.loggedInOnProducts(username);
    menu.open();
    menu.select(MenuOptions.LOGOUT);
    assertThat(loginPage.isLoaded()).as("Pré-condição: logout concluído").isTrue();
  }

  @Quando("ele se autentica com a senha {string}")
  public void authenticatesWithPassword(String password) {
    loginPage.login(context.username(), password);
  }

  @Quando("ele tenta se autenticar com usuário {string} e senha {string}")
  public void triesToAuthenticate(String username, String password) {
    loginPage.login(username, password);
  }

  @Quando("ele digita a senha {string}")
  public void typesPassword(String password) {
    loginPage.typePassword(password);
  }

  @Quando("ele acessa diretamente a página {string}")
  public void accessesPageDirectly(String path) {
    browser.openPath(path);
  }

  @Quando("ele tenta retornar à página de produtos pelo histórico do navegador")
  public void goesBackInHistory() {
    browser.back();
  }

  @Então("o conteúdo do campo de senha é exibido mascarado")
  public void passwordIsMasked() {
    assertThat(loginPage.passwordFieldType())
        .as("Atributo 'type' do campo de senha")
        .isEqualTo(MASKED_INPUT_TYPE);
  }
}
