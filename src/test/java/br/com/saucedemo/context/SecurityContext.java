package br.com.saucedemo.context;

import java.util.Map;

/** Cabeçalhos capturados no passo "Quando" e avaliados no passo "Então" do mesmo cenário. */
public class SecurityContext {

  private Map<String, String> responseHeaders;

  public Map<String, String> responseHeaders() {
    if (responseHeaders == null) {
      throw new IllegalStateException("Nenhum cabeçalho de resposta capturado no cenário");
    }
    return responseHeaders;
  }

  public void setResponseHeaders(Map<String, String> responseHeaders) {
    this.responseHeaders = responseHeaders;
  }
}
