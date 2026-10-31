package br.com.saucedemo.pages;

import br.com.saucedemo.pages.base.BasePage;
import br.com.saucedemo.ui.HeaderUi;
import br.com.saucedemo.ui.MenuUi;
import java.util.List;

/** Menu lateral (hambúrguer) disponível em todas as páginas autenticadas. */
public class MenuComponent extends BasePage {

  public void open() {
    click(HeaderUi.MENU_BUTTON);
  }

  public List<String> visibleOptions() {
    return textsOf(MenuUi.OPTIONS);
  }

  public void select(String option) {
    click(MenuUi.option(option));
  }
}
