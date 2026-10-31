package br.com.saucedemo.security;

import br.com.saucedemo.config.Environment;
import br.com.saucedemo.pages.base.BasePage;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.Event;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.json.Json;

/**
 * Captura os cabeçalhos da resposta do documento principal via Chrome DevTools Protocol, apenas
 * observando a navegação normal (nenhuma requisição extra ou alterada).
 *
 * <p>Usa os comandos CDP pelo nome ({@code Network.enable} / {@code Network.responseReceived}) em
 * vez das classes geradas por versão ({@code devtools.v1xx}), para não quebrar quando o Chrome do
 * CI for atualizado.
 */
public class SecurityHeadersInspector extends BasePage {

  private static final Event<Map<String, Object>> RESPONSE_RECEIVED =
      new Event<>("Network.responseReceived", input -> input.read(Json.MAP_TYPE));

  public boolean isSupported() {
    return Environment.browser().isChromium();
  }

  /**
   * Navega até {@code path} e devolve os cabeçalhos da resposta do documento (nomes em minúsculas).
   */
  public Map<String, String> documentResponseHeaders(String path) {
    DevTools devTools = ((HasDevTools) driver()).getDevTools();
    devTools.createSessionIfThereIsNotOne();
    CompletableFuture<Map<String, String>> headers = new CompletableFuture<>();
    devTools.addListener(
        RESPONSE_RECEIVED,
        event -> {
          if ("Document".equals(event.get("type"))) {
            Map<?, ?> response = (Map<?, ?>) event.get("response");
            headers.complete(lowerCaseNames((Map<?, ?>) response.get("headers")));
          }
        });
    devTools.send(new Command<>("Network.enable", Map.of()));
    try {
      open(path);
      return headers.get(Environment.timeout().toMillis(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Captura de cabeçalhos interrompida", e);
    } catch (ExecutionException | TimeoutException e) {
      throw new IllegalStateException("Resposta do documento não capturada via CDP: " + path, e);
    } finally {
      devTools.clearListeners();
      devTools.send(new Command<>("Network.disable", Map.of()));
    }
  }

  private static Map<String, String> lowerCaseNames(Map<?, ?> raw) {
    Map<String, String> headers = new TreeMap<>();
    raw.forEach(
        (name, value) -> headers.put(name.toString().toLowerCase(Locale.ROOT), value.toString()));
    return headers;
  }
}
