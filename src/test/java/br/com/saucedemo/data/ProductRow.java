package br.com.saucedemo.data;

/** Produto como exibido em uma listagem: nome e preço formatado (ex.: "$29.99"). */
public record ProductRow(String name, String price) {}
