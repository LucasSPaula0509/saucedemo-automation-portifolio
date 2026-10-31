package br.com.saucedemo.context;

/**
 * Estado compartilhado entre as classes de steps de um mesmo cenário. O PicoContainer cria uma
 * instância nova por cenário, então nada vaza entre cenários (nem entre threads).
 */
public class ScenarioContext {

  private String username;

  public String username() {
    if (username == null) {
      throw new IllegalStateException("Nenhum usuário definido no cenário (passo 'Dado' ausente?)");
    }
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
