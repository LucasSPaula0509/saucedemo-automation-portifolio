package br.com.saucedemo.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Lê as configurações do projeto respeitando a precedência: propriedade de sistema ({@code -Dchave})
 * → variável de ambiente ({@code CHAVE_COM_UNDERSCORE}) → {@code config.properties}.
 */
public final class ConfigReader {

  private static final String CONFIG_FILE = "config.properties";
  private static final Properties FILE_PROPERTIES = loadFile();

  private ConfigReader() {}

  public static String get(String key) {
    return find(key)
        .orElseThrow(() -> new IllegalStateException("Configuração obrigatória ausente: " + key));
  }

  public static Optional<String> find(String key) {
    return Stream.of(
            System.getProperty(key), System.getenv(toEnvName(key)), FILE_PROPERTIES.getProperty(key))
        .filter(value -> value != null && !value.isBlank())
        .map(String::trim)
        .findFirst();
  }

  public static int getInt(String key) {
    return Integer.parseInt(get(key));
  }

  public static boolean getBoolean(String key) {
    return Boolean.parseBoolean(get(key));
  }

  static String toEnvName(String key) {
    return key.toUpperCase(Locale.ROOT).replace('.', '_');
  }

  private static Properties loadFile() {
    try (InputStream stream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
      if (stream == null) {
        throw new IllegalStateException(CONFIG_FILE + " não encontrado no classpath");
      }
      Properties properties = new Properties();
      properties.load(new InputStreamReader(stream, UTF_8));
      return properties;
    } catch (IOException e) {
      throw new UncheckedIOException("Falha ao ler " + CONFIG_FILE, e);
    }
  }
}
