package br.com.saucedemo.steps;

import br.com.saucedemo.data.CartItemRow;
import br.com.saucedemo.data.ProductRow;
import io.cucumber.java.DataTableType;
import java.util.Map;

/** Converte as tabelas do Gherkin (cabeçalhos em português) em records Java. */
public class DataTableTypes {

  @DataTableType
  public ProductRow productRow(Map<String, String> row) {
    return new ProductRow(row.get("produto"), row.get("preco"));
  }

  @DataTableType
  public CartItemRow cartItemRow(Map<String, String> row) {
    return new CartItemRow(row.get("quantidade"), row.get("produto"), row.get("preco"));
  }
}
