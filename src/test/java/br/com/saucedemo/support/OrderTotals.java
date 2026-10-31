package br.com.saucedemo.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Regra de cálculo do resumo do pedido: subtotal = soma dos itens; imposto = 8% do subtotal,
 * arredondado a 2 casas (HALF_UP); total = subtotal + imposto.
 */
public record OrderTotals(List<BigDecimal> itemPrices, BigDecimal subtotal, BigDecimal tax, BigDecimal total) {

  public static final BigDecimal TAX_RATE = new BigDecimal("0.08");

  public static OrderTotals fromItemPrices(List<String> formattedPrices) {
    List<BigDecimal> prices = formattedPrices.stream().map(Money::parse).toList();
    BigDecimal subtotal = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    return new OrderTotals(prices, subtotal, tax, subtotal.add(tax));
  }

  public String describe() {
    return """
        Itens: %s
        Subtotal (soma dos itens): %s
        Imposto (%s%% de %s, HALF_UP): %s
        Total (subtotal + imposto): %s
        """
        .formatted(
            itemPrices.stream().map(Money::format).toList(),
            Money.format(subtotal),
            TAX_RATE.movePointRight(2).stripTrailingZeros().toPlainString(),
            Money.format(subtotal),
            Money.format(tax),
            Money.format(total));
  }
}
