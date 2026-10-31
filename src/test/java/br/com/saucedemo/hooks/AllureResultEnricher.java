package br.com.saucedemo.hooks;

import br.com.saucedemo.config.Environment;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.util.ResultsUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Ajusta cada resultado logo antes de o Allure gravá-lo (registrado em {@code
 * META-INF/services/io.qameta.allure.listener.TestLifecycleListener}).
 *
 * <ul>
 *   <li>Label Feature a partir da tag da funcionalidade ({@code @login}, {@code @produtos}...).
 *   <li>Parâmetro "Navegador" e {@code historyId} por navegador, para que a mesma execução em
 *       Chrome e Firefox não apareça como retentativa no relatório consolidado do CI.
 *   <li>Cenários {@code @defeito} recebem o link de issue mapeado em {@code
 *       known-defects.properties}; quando falham, a mensagem ganha o prefixo {@code [Defeito
 *       conhecido DEF-xxx]}, usado pela categoria "Defeitos conhecidos" do {@code categories.json}.
 * </ul>
 */
public class AllureResultEnricher implements TestLifecycleListener {

  private static final String TAG_LABEL = "tag";
  private static final String FEATURE_LABEL = "feature";
  private static final String DEFECT_TAG = "defeito";
  private static final Map<String, String> FEATURES =
      Map.of(
          "login", "Login",
          "produtos", "Produtos",
          "carrinho", "Carrinho",
          "checkout", "Checkout",
          "performance", "Performance",
          "seguranca", "Segurança");
  private static final Properties KNOWN_DEFECTS = load("known-defects.properties");

  @Override
  public void beforeTestWrite(TestResult result) {
    List<String> tags =
        result.getLabels().stream()
            .filter(label -> TAG_LABEL.equals(label.getName()))
            .map(Label::getValue)
            .toList();
    tags.stream()
        .map(FEATURES::get)
        .filter(Objects::nonNull)
        .findFirst()
        .ifPresent(feature -> replaceFeature(result, feature));
    separateByBrowser(result);
    if (tags.contains(DEFECT_TAG)) {
      markKnownDefect(result, defectId(tags));
    }
  }

  private static void replaceFeature(TestResult result, String feature) {
    result.getLabels().removeIf(label -> FEATURE_LABEL.equals(label.getName()));
    result.getLabels().add(ResultsUtils.createFeatureLabel(feature));
  }

  private static void separateByBrowser(TestResult result) {
    String browser = Environment.browser().name().toLowerCase();
    // A lista de parâmetros de cenários sem exemplos é imutável: cria uma nova.
    List<Parameter> parameters = new ArrayList<>(result.getParameters());
    parameters.add(new Parameter().setName("Navegador").setValue(browser));
    result.setParameters(parameters);
    result.setHistoryId(result.getHistoryId() + ":" + browser);
  }

  private static Optional<String> defectId(List<String> tags) {
    return tags.stream().map(KNOWN_DEFECTS::getProperty).filter(Objects::nonNull).findFirst();
  }

  private static void markKnownDefect(TestResult result, Optional<String> defectId) {
    defectId.ifPresent(id -> result.getLinks().add(ResultsUtils.createIssueLink(id)));
    StatusDetails details = result.getStatusDetails();
    if (details == null
        || !(result.getStatus() == Status.FAILED || result.getStatus() == Status.BROKEN)) {
      return;
    }
    details.setKnown(true);
    details.setMessage(
        "[Defeito conhecido %s] %s"
            .formatted(defectId.orElse("sem ID"), Objects.toString(details.getMessage(), "")));
  }

  private static Properties load(String resource) {
    Properties properties = new Properties();
    try (InputStream in =
        AllureResultEnricher.class.getClassLoader().getResourceAsStream(resource)) {
      if (in != null) {
        properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Falha ao ler " + resource, e);
    }
    return properties;
  }
}
