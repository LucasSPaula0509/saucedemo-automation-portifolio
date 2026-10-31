package br.com.saucedemo.security;

import java.util.Optional;

/** Atributos de proteção de um cookie, como o navegador os enxerga. */
public record CookieAttributes(
    String name, boolean secure, boolean httpOnly, Optional<String> sameSite) {

  public String toJson() {
    return """
        {
          "nome": "%s",
          "secure": %b,
          "httpOnly": %b,
          "sameSite": %s
        }"""
        .formatted(
            name, secure, httpOnly, sameSite.map(value -> "\"" + value + "\"").orElse("null"));
  }
}
