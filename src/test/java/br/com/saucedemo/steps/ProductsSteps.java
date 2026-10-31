package br.com.saucedemo.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.saucedemo.data.ProductRow;
import br.com.saucedemo.pages.ActionButtonsComponent;
import br.com.saucedemo.pages.BrowserNavigation;
import br.com.saucedemo.pages.MenuComponent;
import br.com.saucedemo.pages.ProductDetailPage;
import br.com.saucedemo.pages.ProductsPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;

public class ProductsSteps {

  private static final String NAME_COLUMN = "produto";
  private static final String PRICE_COLUMN = "preco";
  private static final String OPTION_COLUMN = "opcao";

  private final ShopperJourney journey;
  private final ProductsPage productsPage;
  private final ProductDetailPage detailPage;
  private final MenuComponent menu;
  private final ActionButtonsComponent actionButtons;
  private final BrowserNavigation browser;

  public ProductsSteps(
      ShopperJourney journey,
      ProductsPage productsPage,
      ProductDetailPage detailPage,
      MenuComponent menu,
      ActionButtonsComponent actionButtons,
      BrowserNavigation browser) {
    this.journey = journey;
    this.productsPage = productsPage;
    this.detailPage = detailPage;
    this.menu = menu;
    this.actionButtons = actionButtons;
    this.browser = browser;
  }

  @Dado("que o usuário {string} está na página de produtos")
  public void userIsOnProductsPage(String username) {
    journey.loggedInOnProducts(username);
  }

  @Dado("que o usuário {string} ordenou os produtos por {string}")
  public void userHasSortedProducts(String username, String sortOption) {
    journey.loggedInOnProducts(username);
    productsPage.sortBy(sortOption);
  }

  @Dado("que o usuário {string} está no detalhe do produto {string}")
  public void userIsOnProductDetail(String username, String product) {
    journey.loggedInOnProducts(username);
    productsPage.openDetail(product);
  }

  @Dado("que o usuário {string} está com o menu lateral aberto")
  public void userHasSideMenuOpen(String username) {
    journey.loggedInOnProducts(username);
    menu.open();
  }

  @Quando("ele acessa a página de produtos")
  public void accessesProductsPage() {
    productsPage.open();
  }

  @Quando("ele ordena os produtos por {string}")
  public void sortsProducts(String sortOption) {
    productsPage.sortBy(sortOption);
  }

  @Quando("ele abre o detalhe de um produto e volta para a lista com {string}")
  public void opensAnyDetailAndGoesBack(String backButtonLabel) {
    productsPage.openDetail(productsPage.productNames().getFirst());
    actionButtons.press(backButtonLabel);
  }

  @Quando("ele abre o detalhe do produto {string}")
  public void opensProductDetail(String product) {
    productsPage.openDetail(product);
  }

  @Quando("ele abre o menu lateral")
  public void opensSideMenu() {
    menu.open();
  }

  @Então("os produtos são exibidos com nome e preço")
  public void productsAreDisplayedWithNameAndPrice(DataTable expected) {
    assertProductListMatches(expected);
  }

  @Então("os produtos são exibidos na ordem")
  public void productsAreDisplayedInOrder(DataTable expected) {
    assertProductListMatches(expected);
  }

  @Então("a ordenação {string} está selecionada")
  public void sortOptionIsSelected(String sortOption) {
    assertThat(productsPage.activeSort()).as("Ordenação selecionada").isEqualTo(sortOption);
  }

  @Então("o detalhe exibe o nome {string} com o preço {string}")
  public void detailShowsNameAndPrice(String name, String price) {
    SoftAssertions.assertSoftly(
        softly -> {
          softly.assertThat(detailPage.isLoaded()).as("Página de detalhe carregada").isTrue();
          softly.assertThat(detailPage.name()).as("Nome no detalhe").isEqualTo(name);
          softly.assertThat(detailPage.price()).as("Preço no detalhe").isEqualTo(price);
        });
  }

  @Então("as opções do menu são exibidas")
  public void sideMenuOptionsAreDisplayed(DataTable expected) {
    List<String> expectedOptions =
        expected.asMaps().stream().map(row -> row.get(OPTION_COLUMN)).toList();
    assertThat(menu.visibleOptions())
        .as("Opções do menu lateral")
        .containsExactlyElementsOf(expectedOptions);
  }

  @Então("o site {string} é exibido")
  public void externalSiteIsDisplayed(String url) {
    assertThat(browser.externalSiteUrl()).as("URL do site externo").isEqualTo(url);
  }

  /**
   * Compara a lista completa e ordenada. A coluna de preço só é verificada quando presente na
   * tabela do cenário. Todas as divergências são reportadas de uma vez.
   */
  private void assertProductListMatches(DataTable expected) {
    List<Map<String, String>> expectedRows = expected.asMaps();
    boolean checkPrices = expected.row(0).contains(PRICE_COLUMN);
    List<ProductRow> actual = productsPage.products();

    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(actual).as("Quantidade de produtos exibidos").hasSameSizeAs(expectedRows);
    for (int i = 0; i < Math.min(actual.size(), expectedRows.size()); i++) {
      Map<String, String> row = expectedRows.get(i);
      softly
          .assertThat(actual.get(i).name())
          .as("Produto na posição %d", i + 1)
          .isEqualTo(row.get(NAME_COLUMN));
      if (checkPrices) {
        softly
            .assertThat(actual.get(i).price())
            .as("Preço na posição %d", i + 1)
            .isEqualTo(row.get(PRICE_COLUMN));
      }
    }
    softly.assertAll();
  }
}
