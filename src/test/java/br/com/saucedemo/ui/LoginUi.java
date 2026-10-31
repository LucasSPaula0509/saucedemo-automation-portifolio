package br.com.saucedemo.ui;

import org.openqa.selenium.By;

public final class LoginUi {

  public static final By USERNAME = DataTest.of("username");
  public static final By PASSWORD = DataTest.of("password");
  public static final By LOGIN_BUTTON = DataTest.of("login-button");

  private LoginUi() {}
}
