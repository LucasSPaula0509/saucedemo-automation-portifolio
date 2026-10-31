package br.com.saucedemo.pages;

import static br.com.saucedemo.ui.LoginUi.LOGIN_BUTTON;
import static br.com.saucedemo.ui.LoginUi.PASSWORD;
import static br.com.saucedemo.ui.LoginUi.USERNAME;

import br.com.saucedemo.data.Passwords;
import br.com.saucedemo.data.Paths;
import br.com.saucedemo.pages.base.BasePage;

public class LoginPage extends BasePage {

  public void open() {
    open(Paths.LOGIN);
  }

  public void login(String username, String password) {
    type(USERNAME, username);
    type(PASSWORD, password);
    click(LOGIN_BUTTON);
  }

  /** Atalho reutilizável para pré-condições: abre a tela de login e autentica pela UI. */
  public void loginAs(String username) {
    open();
    login(username, Passwords.DEFAULT);
  }

  public void typePassword(String password) {
    type(PASSWORD, password);
  }

  public String passwordFieldType() {
    return attributeOf(PASSWORD, "type");
  }

  public String passwordFieldAutocomplete() {
    return attributeOf(PASSWORD, "autocomplete");
  }

  public boolean isLoaded() {
    return isDisplayed(LOGIN_BUTTON) && currentPath().equals(Paths.LOGIN);
  }
}
