package br.com.saucedemo.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import br.com.saucedemo.config.Environment;
import br.com.saucedemo.context.SecurityContext;
import br.com.saucedemo.data.Cookies;
import br.com.saucedemo.data.Paths;
import br.com.saucedemo.pages.BrowserNavigation;
import br.com.saucedemo.pages.LoginPage;
import br.com.saucedemo.security.CookieAttributes;
import br.com.saucedemo.security.SecurityHeadersInspector;
import br.com.saucedemo.security.SessionCookieInspector;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.qameta.allure.Allure;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verificações passivas: só leem o que o navegador já recebeu numa navegação normal. Achados são
 * anexados ao Allure e só reprovam o cenário com {@code security.fail.on.findings=true}.
 */
public class SecuritySteps {

  private static final Logger LOG = LoggerFactory.getLogger(SecuritySteps.class);
  private static final Set<String> ADEQUATE_PASSWORD_AUTOCOMPLETE =
      Set.of("current-password", "new-password", "off");

  private final SecurityContext security;
  private final BrowserNavigation browser;
  private final LoginPage loginPage;
  private final SessionCookieInspector cookies;
  private final SecurityHeadersInspector headersInspector;

  public SecuritySteps(
      SecurityContext security,
      BrowserNavigation browser,
      LoginPage loginPage,
      SessionCookieInspector cookies,
      SecurityHeadersInspector headersInspector) {
    this.security = security;
    this.browser = browser;
    this.loginPage = loginPage;
    this.cookies = cookies;
    this.headersInspector = headersInspector;
  }

  @Quando("o visitante acessa o site pelo protocolo HTTP")
  public void visitorAccessesOverHttp() {
    browser.openOverHttp();
  }

  @Então("a página é servida via HTTPS")
  public void pageIsServedOverHttps() {
    assertThat(browser.url()).as("URL após acessar via HTTP").startsWith("https://");
  }

  @Então("os atributos Secure, HttpOnly e SameSite do cookie de sessão são verificados")
  public void sessionCookieAttributesAreChecked() {
    Optional<CookieAttributes> cookie = cookies.cookie(Cookies.SESSION);
    assertThat(cookie).as("Cookie de sessão '%s'", Cookies.SESSION).isPresent();
    CookieAttributes attributes = cookie.orElseThrow();

    List<String> findings = new ArrayList<>();
    if (!attributes.secure()) {
      findings.add("Cookie '%s' sem o atributo Secure".formatted(attributes.name()));
    }
    if (!attributes.httpOnly()) {
      findings.add("Cookie '%s' sem o atributo HttpOnly".formatted(attributes.name()));
    }
    if (attributes.sameSite().filter(value -> !value.equalsIgnoreCase("None")).isEmpty()) {
      findings.add(
          "Cookie '%s' com SameSite %s"
              .formatted(attributes.name(), attributes.sameSite().orElse("ausente")));
    }
    report("Cookie de sessão", attributes.toJson(), findings);
  }

  @Então("o atributo autocomplete do campo de senha é verificado")
  public void passwordAutocompleteIsChecked() {
    String autocomplete = loginPage.passwordFieldAutocomplete();
    List<String> findings = new ArrayList<>();
    if (autocomplete == null || !ADEQUATE_PASSWORD_AUTOCOMPLETE.contains(autocomplete.trim())) {
      findings.add(
          "Campo de senha com autocomplete %s (esperado: %s)"
              .formatted(
                  autocomplete == null ? "ausente" : "\"" + autocomplete + "\"",
                  String.join(", ", ADEQUATE_PASSWORD_AUTOCOMPLETE)));
    }
    report("Autocomplete do campo de senha", "autocomplete=" + autocomplete, findings);
  }

  @Quando("o visitante abre a página de login registrando os cabeçalhos da resposta")
  public void opensLoginPageCapturingHeaders() {
    assumeThat(headersInspector.isSupported())
        .as("Cabeçalhos via CDP exigem Chrome ou Edge (navegador atual: %s)", Environment.browser())
        .isTrue();
    security.setResponseHeaders(headersInspector.documentResponseHeaders(Paths.LOGIN));
  }

  @Então("os cabeçalhos de segurança da resposta são verificados")
  public void securityHeadersAreChecked() {
    Map<String, String> headers = security.responseHeaders();
    List<String> findings = new ArrayList<>();
    if (!headers.containsKey("content-security-policy")) {
      findings.add("Cabeçalho Content-Security-Policy ausente");
    }
    if (!headers.containsKey("x-frame-options")
        && !headers.getOrDefault("content-security-policy", "").contains("frame-ancestors")) {
      findings.add("Proteção contra clickjacking ausente (X-Frame-Options ou CSP frame-ancestors)");
    }
    if (!"nosniff".equalsIgnoreCase(headers.getOrDefault("x-content-type-options", "").trim())) {
      findings.add("Cabeçalho X-Content-Type-Options diferente de 'nosniff' ou ausente");
    }
    if (!headers.containsKey("strict-transport-security")) {
      findings.add("Cabeçalho Strict-Transport-Security (HSTS) ausente");
    }
    String evidence =
        headers.entrySet().stream()
            .map(header -> header.getKey() + ": " + header.getValue())
            .collect(Collectors.joining("\n"));
    report("Cabeçalhos de segurança", evidence, findings);
  }

  private void report(String check, String evidence, List<String> findings) {
    Allure.addAttachment(check + " - evidência", "text/plain", evidence);
    Allure.addAttachment(
        check + " - achados",
        "text/plain",
        findings.isEmpty() ? "Nenhum achado" : String.join("\n", findings));
    findings.forEach(finding -> LOG.warn("Achado de segurança [{}]: {}", check, finding));
    if (Environment.failOnSecurityFindings()) {
      assertThat(findings).as("Achados de segurança - %s", check).isEmpty();
    }
  }
}
