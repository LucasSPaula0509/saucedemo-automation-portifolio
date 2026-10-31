package br.com.saucedemo.hooks;

import static java.nio.charset.StandardCharsets.UTF_8;

import br.com.saucedemo.config.Environment;
import br.com.saucedemo.driver.DriverManager;
import io.cucumber.java.Scenario;
import java.util.stream.Collectors;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

/** Anexa evidências do navegador ao cenário (o plugin do Allure as converte em anexos). */
class Evidence {

  void screenshot(Scenario scenario, String name) {
    byte[] png = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BYTES);
    scenario.attach(png, "image/png", name);
  }

  void failureContext(Scenario scenario) {
    String suffix = " - " + scenario.getName();
    scenario.attach(driver().getCurrentUrl(), "text/uri-list", "URL" + suffix);
    scenario.attach(driver().getPageSource().getBytes(UTF_8), "text/html", "HTML" + suffix);
    scenario.attach(browserConsole(), "text/plain", "Console do navegador" + suffix);
  }

  /** Logs de console só são expostos pelos navegadores Chromium (Chrome/Edge). */
  private String browserConsole() {
    if (!Environment.browser().isChromium()) {
      return "Logs de console indisponíveis para " + Environment.browser();
    }
    String logs =
        driver().manage().logs().get(LogType.BROWSER).getAll().stream()
            .map(LogEntry::toString)
            .collect(Collectors.joining(System.lineSeparator()));
    return logs.isEmpty() ? "(console vazio)" : logs;
  }

  private WebDriver driver() {
    return DriverManager.get();
  }
}
