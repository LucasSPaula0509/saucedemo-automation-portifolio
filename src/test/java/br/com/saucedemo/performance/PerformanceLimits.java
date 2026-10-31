package br.com.saucedemo.performance;

import br.com.saucedemo.config.ConfigReader;
import java.time.Duration;

/** Limites de performance em {@code config.properties} (sobrescrevíveis com -D / variável). */
public final class PerformanceLimits {

  private PerformanceLimits() {}

  public static Duration loginPageLoad() {
    return millis("perf.login.page.max.ms");
  }

  public static Duration loginToInventory() {
    return millis("perf.login.to.inventory.max.ms");
  }

  public static Duration largestContentfulPaint() {
    return millis("perf.lcp.max.ms");
  }

  private static Duration millis(String key) {
    return Duration.ofMillis(ConfigReader.getInt(key));
  }
}
