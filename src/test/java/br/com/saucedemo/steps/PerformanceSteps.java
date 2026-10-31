package br.com.saucedemo.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.saucedemo.context.PerformanceContext;
import br.com.saucedemo.context.ScenarioContext;
import br.com.saucedemo.pages.LoginPage;
import br.com.saucedemo.pages.ProductsPage;
import br.com.saucedemo.performance.NavigationMetrics;
import br.com.saucedemo.performance.NavigationMetricsCollector;
import br.com.saucedemo.performance.PerformanceLimits;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.qameta.allure.Allure;
import java.time.Duration;

public class PerformanceSteps {

  private final ScenarioContext context;
  private final PerformanceContext measurements;
  private final LoginPage loginPage;
  private final ProductsPage productsPage;
  private final NavigationMetricsCollector collector;

  public PerformanceSteps(
      ScenarioContext context,
      PerformanceContext measurements,
      LoginPage loginPage,
      ProductsPage productsPage,
      NavigationMetricsCollector collector) {
    this.context = context;
    this.measurements = measurements;
    this.loginPage = loginPage;
    this.productsPage = productsPage;
    this.collector = collector;
  }

  @Quando("o visitante abre a página de login medindo o carregamento")
  public void opensLoginPageMeasuring() {
    loginPage.open();
    NavigationMetrics metrics = collector.collect();
    measurements.setNavigation(metrics);
    Allure.addAttachment(
        "Métricas de navegação - login", "application/json", metrics.toJson(), ".json");
  }

  /**
   * Tempo percebido do clique em Login até a lista de produtos visível, medido pelo teste (inclui a
   * latência do WebDriver, por isso o limite é folgado).
   */
  @Quando("ele se autentica com a senha {string} medindo o tempo até a página de produtos")
  public void logsInMeasuringTimeToProducts(String password) {
    long start = System.nanoTime();
    loginPage.login(context.username(), password);
    boolean productsLoaded = productsPage.isLoaded();
    Duration elapsed = Duration.ofNanos(System.nanoTime() - start);
    measurements.setLoginToInventory(elapsed);
    Allure.addAttachment(
        "Tempo do login até a página de produtos",
        "application/json",
        """
        {
          "usuario": "%s",
          "tempoMs": %d,
          "limiteMs": %d,
          "paginaDeProdutosExibida": %b
        }"""
            .formatted(
                context.username(),
                elapsed.toMillis(),
                PerformanceLimits.loginToInventory().toMillis(),
                productsLoaded),
        ".json");
  }

  @Então("a página de login carrega dentro do limite configurado")
  public void loginPageLoadsWithinLimit() {
    assertThat(measurements.navigation().loadMs())
        .as("Carregamento da página de login (ms)")
        .isLessThanOrEqualTo(PerformanceLimits.loginPageLoad().toMillis());
  }

  @Então("o Largest Contentful Paint fica dentro do limite configurado")
  public void largestContentfulPaintWithinLimit() {
    assertThat(measurements.navigation().lcpMs())
        .as("Largest Contentful Paint (ms)")
        .hasValueSatisfying(
            lcp ->
                assertThat(lcp)
                    .isLessThanOrEqualTo(PerformanceLimits.largestContentfulPaint().toMillis()));
  }

  @Então("o tempo até a página de produtos fica dentro do limite configurado")
  public void timeToProductsWithinLimit() {
    assertThat(measurements.loginToInventory())
        .as("Tempo do login até a página de produtos")
        .isLessThanOrEqualTo(PerformanceLimits.loginToInventory());
  }

  @Então("o tempo até a página de produtos é sinalizado como acima do limite configurado")
  public void timeToProductsFlaggedAsSlow() {
    assertThat(measurements.loginToInventory())
        .as("Tempo do login até a página de produtos (usuário com lentidão)")
        .isGreaterThan(PerformanceLimits.loginToInventory());
  }
}
