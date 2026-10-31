package br.com.saucedemo.hooks;

import br.com.saucedemo.config.Environment;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Grava o {@code environment.properties} do Allure uma vez por execução. */
final class AllureEnvironment {

  private static final Logger LOG = LoggerFactory.getLogger(AllureEnvironment.class);
  private static final AtomicBoolean WRITTEN = new AtomicBoolean();

  private AllureEnvironment() {}

  static void writeOnce(WebDriver driver) {
    if (!WRITTEN.compareAndSet(false, true)) {
      return;
    }
    Map<String, String> env = new LinkedHashMap<>();
    if (driver instanceof HasCapabilities hasCapabilities) {
      Capabilities caps = hasCapabilities.getCapabilities();
      env.put("Browser", caps.getBrowserName());
      env.put("Browser.Version", caps.getBrowserVersion());
    } else {
      env.put("Browser", Environment.browser().name().toLowerCase());
    }
    env.put("Base.URL", Environment.baseUrl());
    env.put("Headless", String.valueOf(Environment.headless()));
    env.put("Grid", Environment.gridUrl().orElse("local"));
    env.put("OS", System.getProperty("os.name"));
    env.put("Java", System.getProperty("java.version"));

    Path dir = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
    String content =
        env.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("\n", "", "\n"));
    try {
      Files.createDirectories(dir);
      Files.writeString(dir.resolve("environment.properties"), content, StandardCharsets.UTF_8);
    } catch (IOException e) {
      LOG.warn("Não foi possível gravar environment.properties do Allure", e);
    }
  }
}
