package br.com.saucedemo.driver;

import org.openqa.selenium.WebDriver;

/**
 * Mantém um {@link WebDriver} por thread, permitindo a execução paralela de cenários sem
 * compartilhamento de sessão.
 */
public final class DriverManager {

  private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

  private DriverManager() {}

  public static void start() {
    DRIVER.set(DriverFactory.create());
  }

  public static WebDriver get() {
    WebDriver driver = DRIVER.get();
    if (driver == null) {
      throw new IllegalStateException("Nenhum WebDriver iniciado para a thread atual");
    }
    return driver;
  }

  public static boolean isStarted() {
    return DRIVER.get() != null;
  }

  public static void quit() {
    WebDriver driver = DRIVER.get();
    DRIVER.remove();
    if (driver != null) {
      driver.quit();
    }
  }
}
