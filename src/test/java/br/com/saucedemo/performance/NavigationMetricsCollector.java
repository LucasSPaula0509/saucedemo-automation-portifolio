package br.com.saucedemo.performance;

import br.com.saucedemo.pages.base.BasePage;
import java.util.Map;
import java.util.Optional;

/**
 * Lê as métricas da navegação atual direto do navegador ({@code window.performance}), sem gerar
 * requisições extras ao site.
 */
public class NavigationMetricsCollector extends BasePage {

  /** Retorna {@code null} até o evento {@code load} terminar; a espera repete o script. */
  private static final String NAVIGATION_TIMING =
      """
      const nav = performance.getEntriesByType('navigation')[0];
      if (!nav || nav.loadEventEnd === 0) {
        return null;
      }
      return {
        url: nav.name,
        ttfb: nav.responseStart - nav.startTime,
        domContentLoaded: nav.domContentLoadedEventEnd - nav.startTime,
        load: nav.loadEventEnd - nav.startTime
      };
      """;

  /**
   * LCP só é exposto via {@code PerformanceObserver}; {@code buffered: true} entrega as entradas já
   * registradas. Sem suporte (ou sem entrada em 1 s), devolve {@code null}.
   */
  private static final String LARGEST_CONTENTFUL_PAINT =
      """
      const done = arguments[arguments.length - 1];
      try {
        new PerformanceObserver(list => {
          const entries = list.getEntries();
          done(entries[entries.length - 1].startTime);
        }).observe({ type: 'largest-contentful-paint', buffered: true });
      } catch (unsupported) {
        done(null);
        return;
      }
      setTimeout(() => done(null), 1000);
      """;

  public NavigationMetrics collect() {
    @SuppressWarnings("unchecked")
    Map<String, Object> nav = (Map<String, Object>) scriptResultWhenAvailable(NAVIGATION_TIMING);
    return new NavigationMetrics(
        String.valueOf(nav.get("url")),
        number(nav.get("ttfb")),
        number(nav.get("domContentLoaded")),
        number(nav.get("load")),
        Optional.ofNullable(asyncScriptResult(LARGEST_CONTENTFUL_PAINT))
            .map(NavigationMetricsCollector::number));
  }

  private static double number(Object value) {
    return ((Number) value).doubleValue();
  }
}
