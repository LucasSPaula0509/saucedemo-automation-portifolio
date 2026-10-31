package br.com.saucedemo.driver;

import java.util.Arrays;
import java.util.Locale;

public enum BrowserType {
  CHROME,
  FIREFOX,
  EDGE;

  public static BrowserType from(String name) {
    return Arrays.stream(values())
        .filter(type -> type.name().equalsIgnoreCase(name.trim()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Navegador não suportado: '%s'. Opções: %s"
                        .formatted(name, Arrays.toString(values()).toLowerCase(Locale.ROOT))));
  }

  public boolean isChromium() {
    return this == CHROME || this == EDGE;
  }
}
