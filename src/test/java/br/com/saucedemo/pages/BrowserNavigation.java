package br.com.saucedemo.pages;

import br.com.saucedemo.config.Environment;
import br.com.saucedemo.pages.base.BasePage;
import java.net.URI;

/** Ações do próprio navegador: acesso direto por URL, histórico e recarregamento. */
public class BrowserNavigation extends BasePage {

  public void openPath(String path) {
    open(path);
  }

  public void refresh() {
    driver().navigate().refresh();
  }

  public void back() {
    driver().navigate().back();
  }

  public String url() {
    return currentUrl();
  }

  public String path() {
    return currentPath();
  }

  public String externalSiteUrl() {
    return urlAfterLeaving(URI.create(Environment.baseUrl()).getHost());
  }
}
