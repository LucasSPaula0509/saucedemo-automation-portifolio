package br.com.saucedemo.ui;

import java.util.Map;
import java.util.Optional;
import org.openqa.selenium.By;

public final class MenuUi {

  public static final By OPTIONS = DataTest.endingWith("-sidebar-link");
  public static final By ALL_ITEMS = DataTest.of("inventory-sidebar-link");
  public static final By ABOUT = DataTest.of("about-sidebar-link");
  public static final By LOGOUT = DataTest.of("logout-sidebar-link");
  public static final By RESET_APP_STATE = DataTest.of("reset-sidebar-link");

  private static final Map<String, By> OPTIONS_BY_LABEL =
      Map.of(
          "All Items", ALL_ITEMS,
          "About", ABOUT,
          "Logout", LOGOUT,
          "Reset App State", RESET_APP_STATE);

  private MenuUi() {}

  public static By option(String label) {
    return Optional.ofNullable(OPTIONS_BY_LABEL.get(label))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Opção de menu sem locator mapeado: '%s'. Mapeadas: %s"
                        .formatted(label, OPTIONS_BY_LABEL.keySet())));
  }
}
