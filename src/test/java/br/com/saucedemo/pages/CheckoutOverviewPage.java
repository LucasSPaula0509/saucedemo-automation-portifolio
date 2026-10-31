package br.com.saucedemo.pages;

import static br.com.saucedemo.ui.CheckoutOverviewUi.FINISH;
import static br.com.saucedemo.ui.CheckoutOverviewUi.ITEM_PRICES;
import static br.com.saucedemo.ui.CheckoutOverviewUi.PAYMENT_INFO_LABEL;
import static br.com.saucedemo.ui.CheckoutOverviewUi.PAYMENT_INFO_VALUE;
import static br.com.saucedemo.ui.CheckoutOverviewUi.SHIPPING_INFO_LABEL;
import static br.com.saucedemo.ui.CheckoutOverviewUi.SHIPPING_INFO_VALUE;
import static br.com.saucedemo.ui.CheckoutOverviewUi.SUBTOTAL;
import static br.com.saucedemo.ui.CheckoutOverviewUi.SUMMARY_CONTAINER;
import static br.com.saucedemo.ui.CheckoutOverviewUi.TAX;
import static br.com.saucedemo.ui.CheckoutOverviewUi.TOTAL;

import br.com.saucedemo.data.Paths;
import br.com.saucedemo.pages.base.BasePage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;

public class CheckoutOverviewPage extends BasePage {

  public boolean isLoaded() {
    return isDisplayed(SUMMARY_CONTAINER) && currentPath().equals(Paths.CHECKOUT_STEP_TWO);
  }

  /**
   * Blocos informativos do resumo (pagamento e entrega), indexados pelo rótulo sem os dois-pontos
   * finais exibidos na tela (ex.: "Payment Information:" → "Payment Information").
   */
  public Map<String, String> summaryInfo() {
    Map<String, String> info = new LinkedHashMap<>();
    putInfo(info, PAYMENT_INFO_LABEL, PAYMENT_INFO_VALUE);
    putInfo(info, SHIPPING_INFO_LABEL, SHIPPING_INFO_VALUE);
    return info;
  }

  public List<String> itemPrices() {
    return textsOf(ITEM_PRICES);
  }

  public String subtotal() {
    return textOf(SUBTOTAL);
  }

  public String tax() {
    return textOf(TAX);
  }

  public String total() {
    return textOf(TOTAL);
  }

  public void finish() {
    click(FINISH);
  }

  private void putInfo(Map<String, String> info, By label, By value) {
    info.put(textOf(label).replaceFirst(":\\s*$", ""), textOf(value));
  }
}
