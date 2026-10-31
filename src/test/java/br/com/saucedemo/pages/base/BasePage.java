package br.com.saucedemo.pages.base;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import br.com.saucedemo.config.Environment;
import br.com.saucedemo.driver.DriverManager;
import java.net.URI;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Ações e leituras de estado reutilizadas por todos os Page Objects. Cada ação já embute a espera
 * explícita necessária ({@code WebDriverWait} + {@code ExpectedConditions}); por isso não existem
 * métodos de espera públicos.
 */
public abstract class BasePage {

  protected WebDriver driver() {
    return DriverManager.get();
  }

  protected void open(String path) {
    driver().get(Environment.url(path));
  }

  protected void click(By locator) {
    await().until(elementToBeClickable(locator)).click();
  }

  /** Limpa o campo e digita o texto. Texto vazio mantém o campo vazio. */
  protected void type(By locator, String text) {
    WebElement field = visible(locator);
    field.clear();
    if (!text.isEmpty()) {
      field.sendKeys(text);
    }
  }

  protected void selectByVisibleText(By locator, String option) {
    new Select(visible(locator)).selectByVisibleText(option);
  }

  protected String textOf(By locator) {
    return visible(locator).getText();
  }

  protected List<String> textsOf(By locator) {
    return await().until(visibilityOfAllElementsLocatedBy(locator)).stream()
        .map(WebElement::getText)
        .toList();
  }

  protected String attributeOf(By locator, String attribute) {
    return await().until(presenceOfElementLocated(locator)).getDomAttribute(attribute);
  }

  /** Aguarda o elemento ficar visível até o timeout configurado. */
  protected boolean isDisplayed(By locator) {
    try {
      visible(locator);
      return true;
    } catch (TimeoutException elementNeverBecameVisible) {
      return false;
    }
  }

  /**
   * Aguarda o elemento sumir (ou não existir). Se continuar visível após o timeout, a {@code
   * TimeoutException} do Selenium sobe com a descrição do elemento.
   */
  protected boolean isAbsent(By locator) {
    return await().until(invisibilityOfElementLocated(locator));
  }

  protected String currentUrl() {
    return driver().getCurrentUrl();
  }

  protected String currentPath() {
    return URI.create(currentUrl()).getPath();
  }

  /** URL atual após a navegação sair do domínio da aplicação (links externos). */
  protected String urlAfterLeaving(String domain) {
    await().until(not(urlContains(domain)));
    return currentUrl();
  }

  /**
   * Executa o script até ele retornar um valor não nulo (ex.: métrica que o navegador ainda não
   * gerou).
   */
  protected Object scriptResultWhenAvailable(String script) {
    return await().until(driver -> ((JavascriptExecutor) driver).executeScript(script));
  }

  /** Script assíncrono: o resultado é o valor passado ao callback {@code arguments[last]}. */
  protected Object asyncScriptResult(String script) {
    return ((JavascriptExecutor) driver()).executeAsyncScript(script);
  }

  private WebElement visible(By locator) {
    return await().until(visibilityOfElementLocated(locator));
  }

  private WebDriverWait await() {
    return new WebDriverWait(driver(), Environment.timeout());
  }
}
