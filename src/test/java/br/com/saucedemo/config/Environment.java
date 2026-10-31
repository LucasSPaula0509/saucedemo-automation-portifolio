package br.com.saucedemo.config;

import br.com.saucedemo.driver.BrowserType;
import java.time.Duration;
import java.util.Optional;

/** Acesso tipado às configurações de execução. */
public final class Environment {

  private Environment() {}

  public static String baseUrl() {
    return ConfigReader.get("base.url").replaceAll("/+$", "");
  }

  public static String url(String path) {
    return baseUrl() + (path.startsWith("/") ? path : "/" + path);
  }

  public static BrowserType browser() {
    return BrowserType.from(ConfigReader.get("browser"));
  }

  /**
   * Em CI (variável {@code CI=true}, definida pelo GitHub Actions) a execução é sempre headless.
   */
  public static boolean headless() {
    return ConfigReader.getBoolean("headless") || isCi();
  }

  public static boolean isCi() {
    return ConfigReader.find("ci").map(Boolean::parseBoolean).orElse(false);
  }

  public static Duration timeout() {
    return Duration.ofSeconds(ConfigReader.getInt("timeout.seconds"));
  }

  public static int windowWidth() {
    return ConfigReader.getInt("window.width");
  }

  public static int windowHeight() {
    return ConfigReader.getInt("window.height");
  }

  public static Optional<String> gridUrl() {
    return ConfigReader.find("grid.url");
  }

  public static boolean screenshotEveryStep() {
    return ConfigReader.getBoolean("screenshot.every.step");
  }

  /** {@code true}: achados das verificações passivas de segurança reprovam o cenário. */
  public static boolean failOnSecurityFindings() {
    return ConfigReader.getBoolean("security.fail.on.findings");
  }
}
