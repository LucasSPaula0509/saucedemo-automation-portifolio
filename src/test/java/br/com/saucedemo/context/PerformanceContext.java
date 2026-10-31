package br.com.saucedemo.context;

import br.com.saucedemo.performance.NavigationMetrics;
import java.time.Duration;

/** Medições feitas no passo "Quando" e validadas nos passos "Então" do mesmo cenário. */
public class PerformanceContext {

  private NavigationMetrics navigation;
  private Duration loginToInventory;

  public NavigationMetrics navigation() {
    if (navigation == null) {
      throw new IllegalStateException("Nenhuma métrica de navegação coletada no cenário");
    }
    return navigation;
  }

  public void setNavigation(NavigationMetrics navigation) {
    this.navigation = navigation;
  }

  public Duration loginToInventory() {
    if (loginToInventory == null) {
      throw new IllegalStateException("O tempo do login até a página de produtos não foi medido");
    }
    return loginToInventory;
  }

  public void setLoginToInventory(Duration loginToInventory) {
    this.loginToInventory = loginToInventory;
  }
}
