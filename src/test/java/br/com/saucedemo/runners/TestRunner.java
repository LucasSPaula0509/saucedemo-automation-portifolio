package br.com.saucedemo.runners;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Ponto de entrada da execução. Glue, plugins, filtro de tags padrão e paralelismo estão em {@code
 * junit-platform.properties} e podem ser sobrescritos com {@code -D} (ex.: {@code
 * -Dcucumber.filter.tags="@smoke"}).
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
public class TestRunner {}
