package br.com.saucedemo.security;

import br.com.saucedemo.pages.base.BasePage;
import java.util.Optional;

/** Lê cookies do navegador atual, sem enviar requisições ao site. */
public class SessionCookieInspector extends BasePage {

  public Optional<CookieAttributes> cookie(String name) {
    return Optional.ofNullable(driver().manage().getCookieNamed(name))
        .map(
            cookie ->
                new CookieAttributes(
                    cookie.getName(),
                    cookie.isSecure(),
                    cookie.isHttpOnly(),
                    Optional.ofNullable(cookie.getSameSite())));
  }
}
