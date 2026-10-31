package br.com.saucedemo.pages;

import br.com.saucedemo.pages.base.BasePage;
import br.com.saucedemo.ui.MessageUi;

/** Mensagens exibidas ao usuário: erros (login e checkout) e confirmação do pedido. */
public class FeedbackComponent extends BasePage {

  public String message() {
    return textOf(MessageUi.ANY_FEEDBACK);
  }
}
