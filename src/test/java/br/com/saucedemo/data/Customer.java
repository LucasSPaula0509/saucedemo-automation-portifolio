package br.com.saucedemo.data;

/** Dados pessoais informados no formulário "Checkout: Your Information". */
public record Customer(String firstName, String lastName, String postalCode) {

  public static final Customer DEFAULT = new Customer("Ana", "Teste", "06500000");
}
