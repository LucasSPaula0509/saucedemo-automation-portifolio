package br.com.saucedemo.ui;

import org.openqa.selenium.By;

/** Mensagens de retorno ao usuário: erros de validação e confirmação do pedido. */
public final class MessageUi {

  public static final By ERROR = DataTest.of("error");
  public static final By ANY_FEEDBACK =
      By.cssSelector("[data-test='error'], [data-test='complete-header']");

  private MessageUi() {}
}
