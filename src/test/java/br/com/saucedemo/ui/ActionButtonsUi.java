package br.com.saucedemo.ui;

import java.util.Map;
import java.util.Optional;
import org.openqa.selenium.By;

/**
 * Botões de navegação acionados pelo rótulo visível no Gherkin ({@code ele aciona "Finish"}). O
 * mesmo rótulo pode existir em mais de uma página (ex.: "Cancel"), mas o {@code data-test} é igual.
 */
public final class ActionButtonsUi {

  private static final Map<String, By> BUTTONS_BY_LABEL =
      Map.of(
          "Back to products", ProductDetailUi.BACK_TO_PRODUCTS,
          "Back Home", CheckoutCompleteUi.BACK_HOME,
          "Continue Shopping", CartUi.CONTINUE_SHOPPING,
          "Checkout", CartUi.CHECKOUT,
          "Continue", CheckoutInfoUi.CONTINUE,
          "Cancel", CheckoutInfoUi.CANCEL,
          "Finish", CheckoutOverviewUi.FINISH);

  private ActionButtonsUi() {}

  public static By byLabel(String label) {
    return Optional.ofNullable(BUTTONS_BY_LABEL.get(label))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Botão sem locator mapeado: '%s'. Mapeados: %s"
                        .formatted(label, BUTTONS_BY_LABEL.keySet())));
  }
}
