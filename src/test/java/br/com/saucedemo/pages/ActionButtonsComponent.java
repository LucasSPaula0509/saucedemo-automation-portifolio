package br.com.saucedemo.pages;

import br.com.saucedemo.pages.base.BasePage;
import br.com.saucedemo.ui.ActionButtonsUi;

/** Aciona botões de navegação pelo rótulo visível (ex.: "Finish", "Cancel", "Back Home"). */
public class ActionButtonsComponent extends BasePage {

  public void press(String label) {
    click(ActionButtonsUi.byLabel(label));
  }
}
