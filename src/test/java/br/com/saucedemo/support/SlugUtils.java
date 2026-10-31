package br.com.saucedemo.support;

import java.util.Locale;

/**
 * Converte nomes de produtos no sufixo usado pelo saucedemo nos atributos {@code data-test}. Ex.:
 * "Sauce Labs Backpack" → "sauce-labs-backpack"; "Test.allTheThings() T-Shirt (Red)" →
 * "test.allthethings()-t-shirt-(red)".
 */
public final class SlugUtils {

  private SlugUtils() {}

  public static String toSlug(String productName) {
    return productName.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-");
  }
}
