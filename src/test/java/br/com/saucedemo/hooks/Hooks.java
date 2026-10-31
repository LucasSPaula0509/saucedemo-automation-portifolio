package br.com.saucedemo.hooks;

import br.com.saucedemo.config.Environment;
import br.com.saucedemo.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Ciclo de vida do navegador e coleta de evidências por cenário. */
public class Hooks {

  private static final Logger LOG = LoggerFactory.getLogger(Hooks.class);

  private final Evidence evidence = new Evidence();

  @Before(order = 0)
  public void startBrowser(Scenario scenario) {
    LOG.info("Iniciando cenário: {}", scenario.getName());
    DriverManager.start();
    AllureEnvironment.writeOnce(DriverManager.get());
  }

  /** Screenshot do passo que falhou (ou de todos, com {@code screenshot.every.step=true}). */
  @AfterStep
  public void captureStep(Scenario scenario) {
    if (scenario.isFailed()) {
      evidence.screenshot(scenario, "Falha - " + scenario.getName());
    } else if (Environment.screenshotEveryStep()) {
      evidence.screenshot(scenario, "Passo - " + scenario.getName());
    }
  }

  @After(order = 0)
  public void closeBrowser(Scenario scenario) {
    if (!DriverManager.isStarted()) {
      return;
    }
    try {
      if (scenario.isFailed()) {
        evidence.failureContext(scenario);
      }
    } finally {
      DriverManager.quit();
      LOG.info("Cenário finalizado [{}]: {}", scenario.getStatus(), scenario.getName());
    }
  }
}
