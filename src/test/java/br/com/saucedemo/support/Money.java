package br.com.saucedemo.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Conversão entre textos monetários da tela ("Tax: $0.80", "$29.99") e {@link BigDecimal}. */
public final class Money {

  private static final Pattern AMOUNT = Pattern.compile("\\$(\\d+(?:\\.\\d+)?)");

  private Money() {}

  public static BigDecimal parse(String text) {
    Matcher matcher = AMOUNT.matcher(text);
    if (!matcher.find()) {
      throw new IllegalArgumentException("Valor monetário não encontrado em: '" + text + "'");
    }
    return new BigDecimal(matcher.group(1)).setScale(2, RoundingMode.UNNECESSARY);
  }

  public static String format(BigDecimal amount) {
    return "$" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }
}
