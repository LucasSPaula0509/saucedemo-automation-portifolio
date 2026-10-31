package br.com.saucedemo.ui;

import org.openqa.selenium.By;

/** Fábrica de locators pelo atributo {@code data-test}, o seletor preferencial do projeto. */
final class DataTest {

  private DataTest() {}

  static By of(String value) {
    return By.cssSelector("[data-test='%s']".formatted(value));
  }

  static By endingWith(String suffix) {
    return By.cssSelector("[data-test$='%s']".formatted(suffix));
  }
}
