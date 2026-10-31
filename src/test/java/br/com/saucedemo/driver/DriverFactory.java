package br.com.saucedemo.driver;

import br.com.saucedemo.config.Environment;
import java.util.Map;
import java.util.logging.Level;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Cria instâncias de {@link WebDriver} locais (via Selenium Manager) ou remotas (Selenium Grid,
 * quando {@code grid.url} está definido).
 */
public final class DriverFactory {

  /**
   * Desativa o gerenciador de senhas e o alerta de "senha vazada" do Chrome/Edge, que abre um
   * diálogo nativo após o login no saucedemo e bloqueia as interações.
   */
  private static final Map<String, Object> PASSWORD_MANAGER_PREFS =
      Map.of(
          "credentials_enable_service", false,
          "profile.password_manager_enabled", false,
          "profile.password_manager_leak_detection", false);

  private DriverFactory() {}

  public static WebDriver create() {
    BrowserType browser = Environment.browser();
    WebDriver driver =
        Environment.gridUrl()
            .map(gridUrl -> remote(gridUrl, capabilities(browser)))
            .orElseGet(() -> local(browser));
    driver
        .manage()
        .window()
        .setSize(new Dimension(Environment.windowWidth(), Environment.windowHeight()));
    return driver;
  }

  private static WebDriver local(BrowserType browser) {
    return switch (browser) {
      case CHROME -> new ChromeDriver(chromeOptions());
      case FIREFOX -> new FirefoxDriver(firefoxOptions());
      case EDGE -> new EdgeDriver(edgeOptions());
    };
  }

  private static WebDriver remote(String gridUrl, Capabilities capabilities) {
    return RemoteWebDriver.builder()
        .address(gridUrl)
        .oneOf(capabilities)
        .augmentUsing(new Augmenter())
        .build();
  }

  private static Capabilities capabilities(BrowserType browser) {
    return switch (browser) {
      case CHROME -> chromeOptions();
      case FIREFOX -> firefoxOptions();
      case EDGE -> edgeOptions();
    };
  }

  private static ChromeOptions chromeOptions() {
    ChromeOptions options = chromium(new ChromeOptions(), "--incognito");
    options.setCapability(ChromeOptions.LOGGING_PREFS, browserConsoleLogs());
    return options;
  }

  private static EdgeOptions edgeOptions() {
    EdgeOptions options = chromium(new EdgeOptions(), "--inprivate");
    options.setCapability(EdgeOptions.LOGGING_PREFS, browserConsoleLogs());
    return options;
  }

  private static <T extends ChromiumOptions<?>> T chromium(T options, String privateModeArgument) {
    options.setExperimentalOption("prefs", PASSWORD_MANAGER_PREFS);
    options.addArguments(privateModeArgument, "--disable-search-engine-choice-screen");
    if (Environment.headless()) {
      options.addArguments("--headless=new");
    }
    return options;
  }

  private static FirefoxOptions firefoxOptions() {
    FirefoxOptions options = new FirefoxOptions();
    options.addArguments("-private");
    options.addPreference("signon.rememberSignons", false);
    if (Environment.headless()) {
      options.addArguments("-headless");
    }
    return options;
  }

  private static LoggingPreferences browserConsoleLogs() {
    LoggingPreferences preferences = new LoggingPreferences();
    preferences.enable(LogType.BROWSER, Level.ALL);
    return preferences;
  }
}
