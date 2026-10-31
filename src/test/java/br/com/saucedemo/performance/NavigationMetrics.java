package br.com.saucedemo.performance;

import java.util.Locale;
import java.util.Optional;

/**
 * Métricas de uma navegação completa (Navigation Timing Level 2 + Largest Contentful Paint), em
 * milissegundos a partir do início da navegação.
 *
 * @param lcpMs vazio quando o navegador não expõe LCP
 */
public record NavigationMetrics(
    String url,
    double timeToFirstByteMs,
    double domContentLoadedMs,
    double loadMs,
    Optional<Double> lcpMs) {

  public String toJson() {
    return String.format(
        Locale.ROOT,
        """
        {
          "url": "%s",
          "timeToFirstByteMs": %.1f,
          "domContentLoadedMs": %.1f,
          "loadMs": %.1f,
          "largestContentfulPaintMs": %s
        }""",
        url,
        timeToFirstByteMs,
        domContentLoadedMs,
        loadMs,
        lcpMs.map(value -> String.format(Locale.ROOT, "%.1f", value)).orElse("null"));
  }
}
