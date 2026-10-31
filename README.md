# Swag Labs — Automação de testes front-end

[![Testes E2E](https://github.com/LucasSPaula0509/saucedemo-automation-portifolio/actions/workflows/tests.yml/badge.svg)](https://github.com/LucasSPaula0509/saucedemo-automation-portifolio/actions/workflows/tests.yml)
[![Lint](https://github.com/LucasSPaula0509/saucedemo-automation-portifolio/actions/workflows/lint.yml/badge.svg)](https://github.com/LucasSPaula0509/saucedemo-automation-portifolio/actions/workflows/lint.yml)
[![Performance e Segurança](https://github.com/LucasSPaula0509/saucedemo-automation-portifolio/actions/workflows/security-performance.yml/badge.svg)](https://github.com/LucasSPaula0509/saucedemo-automation-portifolio/actions/workflows/security-performance.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-orange)](https://lucasspaula0509.github.io/saucedemo-automation-portifolio/)

**Allure Report da última execução da `main`:** <https://lucasspaula0509.github.io/saucedemo-automation-portifolio/>

---

## 1. O que é este projeto

Framework de testes automatizados para o front-end da loja de demonstração **Swag Labs**, em <https://www.saucedemo.com>.

Os cenários são escritos em português, no formato BDD (Gherkin), e ficam nos arquivos `.feature`. Cada passo é executado em um navegador real pelo Selenium WebDriver. O resultado é publicado em um relatório Allure.

| Funcionalidade | Arquivo | Cenários |
|---|---|---|
| Login | `features/login.feature` | 9 (CT001–CT009) |
| Produtos | `features/produtos.feature` | 10 (CT001–CT010) |
| Carrinho | `features/carrinho.feature` | 13 (CT001–CT013) |
| Checkout | `features/checkout.feature` | 11 (CT001–CT011) |
| Performance | `features/nao_funcionais/performance.feature` | 3 |
| Segurança passiva | `features/nao_funcionais/seguranca.feature` | 4 |

Os cenários usam esta stack:

| Componente | Ferramenta |
|---|---|
| Linguagem e build | Java 21 e Maven (Maven Wrapper incluído) |
| Navegador | Selenium 4, com Selenium Manager |
| BDD | Cucumber 7, PicoContainer e JUnit 5 Platform Suite |
| Asserções | AssertJ |
| Relatório | Allure 2 |
| Logs | SLF4J e Logback |
| CI | GitHub Actions; Jenkinsfile opcional |
| Performance | Navigation Timing e LCP; Lighthouse CI |
| Segurança | Verificações passivas e OWASP ZAP Baseline |

---

## 2. Pré-requisitos

| Ferramenta | Para quê | Como instalar |
|---|---|---|
| **JDK 21** | compilar e rodar os testes | [Temurin 21](https://adoptium.net/) ou `winget install EclipseAdoptium.Temurin.21.JDK` (Windows) / `sdk install java 21-tem` (Linux/macOS) |
| **Maven** | build | **não precisa instalar**: use o wrapper `./mvnw` (Linux/macOS) ou `mvnw.cmd` (Windows) |
| **Google Chrome** e/ou **Firefox** | executar os cenários | instale normalmente; **não** é preciso baixar o ChromeDriver/GeckoDriver (o Selenium Manager baixa o driver certo sozinho) |
| **Node.js 18+** | *apenas* para rodar o Lighthouse localmente | <https://nodejs.org/> |
| **Git** | clonar o repositório | <https://git-scm.com/> |

```bash
git clone https://github.com/LucasSPaula0509/saucedemo-automation-portifolio.git
cd saucedemo-automation-portifolio
java -version     # deve mostrar 21
./mvnw -v         # na primeira vez, baixa o Maven automaticamente
```

> **Tem outro JDK no `JAVA_HOME`?** Declare o JDK 21 em `~/.m2/toolchains.xml`. Quando esse arquivo existe, o build usa o JDK 21 dele automaticamente (veja [Decisões técnicas](#11-decisões-técnicas)).
>
> ```xml
> <toolchains>
>   <toolchain>
>     <type>jdk</type>
>     <provides><version>21</version></provides>
>     <configuration><jdkHome>C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot</jdkHome></configuration>
>   </toolchain>
> </toolchains>
> ```

---

## 3. Como rodar

> No Windows, troque `./mvnw` por `mvnw.cmd` (ou rode no Git Bash).

```bash
./mvnw clean test                                                     # tudo, exceto @defeito e @performance
./mvnw clean test -Dcucumber.filter.tags="@smoke"                     # só o smoke
./mvnw clean test -Dcucumber.filter.tags="@login and @negativo"       # combinação de tags
./mvnw clean test -Dbrowser=firefox -Dheadless=true                   # outro navegador, sem janela
./mvnw clean test -Dcucumber.filter.tags="@defeito"                   # defeitos conhecidos (devem falhar)
./mvnw clean test -Dcucumber.filter.tags="@performance or @seguranca" # não funcionais
./mvnw allure:serve                                                   # abre o Allure no navegador
```

### Abrindo o relatório Allure

- `./mvnw allure:serve` gera o relatório a partir de `target/allure-results` e abre no navegador.
- `./mvnw allure:report` gera o HTML em `target/site/allure-maven-plugin/index.html`. Abra esse arquivo por um servidor, não direto do disco.
- Também há relatórios do Cucumber em `target/cucumber.html` e `target/cucumber.json`.

Na primeira execução, o Maven baixa o Allure (commandline 2.46.1).

### Configurações

Todas ficam em [`src/test/resources/config.properties`](src/test/resources/config.properties). Cada chave pode ser sobrescrita, nesta ordem de prioridade:

1. `-Dchave=valor` na linha de comando (ex.: `-Dbrowser=edge`)
2. Variável de ambiente com a chave em maiúsculas e `_` no lugar de `.` (ex.: `TIMEOUT_SECONDS=20`)
3. O valor do arquivo

| Chave | Padrão | Descrição |
|---|---|---|
| `base.url` | `https://www.saucedemo.com` | URL do site |
| `browser` | `chrome` | `chrome`, `firefox` ou `edge` |
| `headless` | `false` | sem janela; com `CI=true` é sempre headless |
| `timeout.seconds` | `10` | tempo máximo de cada espera explícita |
| `window.width` / `window.height` | `1920` / `1080` | tamanho da janela |
| `grid.url` | *(vazio)* | URL de um Selenium Grid (vazio = navegador local) |
| `screenshot.every.step` | `false` | `true` anexa screenshot de todos os passos (padrão: só do passo que falhou) |
| `perf.login.page.max.ms` | `3000` | limite de carregamento da página de login |
| `perf.login.to.inventory.max.ms` | `2000` | limite do login até a página de produtos |
| `perf.lcp.max.ms` | `2500` | limite do Largest Contentful Paint |
| `security.fail.on.findings` | `false` | `true` faz achados de segurança reprovarem o cenário |

Paralelismo: são **4 cenários ao mesmo tempo** por padrão, cada um no seu navegador. Para mudar, use `-Dcucumber.execution.parallel.config.fixed.parallelism=2`. Para rodar em sequência, use `-Dcucumber.execution.parallel.enabled=false`.

---

## 4. Estrutura do projeto

```text
├── .github/workflows/
│   ├── tests.yml                  # smoke → regressão (chrome/firefox) + defeitos → Allure no Pages
│   ├── lint.yml                   # actionlint + formatação (Spotless)
│   └── security-performance.yml   # @performance, Lighthouse, @seguranca, ZAP (agendado/manual)
├── Jenkinsfile                    # pipeline opcional equivalente
├── lighthouserc.json              # configuração do Lighthouse CI
├── zap/rules.tsv                  # severidade das regras do ZAP Baseline
├── pom.xml
└── src/test/
    ├── allure/categories.json     # categorias do Allure (copiado para allure-results)
    ├── java/br/com/saucedemo/
    │   ├── config/        # leitura de config.properties (-D > variável de ambiente > arquivo)
    │   ├── driver/        # criação do WebDriver (ThreadLocal, um por cenário)
    │   ├── ui/            # LOCATORS: só "public static final By"
    │   ├── pages/         # PAGE OBJECTS: ações e leitura de estado, sem asserções
    │   │   └── base/BasePage.java   # esperas explícitas ficam só aqui
    │   ├── steps/         # STEP DEFINITIONS em português: orquestram páginas e fazem as asserções
    │   ├── hooks/         # abre/fecha navegador, evidências, integração com o Allure
    │   ├── context/       # estado do cenário compartilhado entre steps (PicoContainer)
    │   ├── data/          # constantes: usuários, senhas, produtos, caminhos, títulos
    │   ├── support/       # utilitários (slug de produto, dinheiro, cálculo do pedido)
    │   ├── performance/   # coletor de métricas de navegação e limites
    │   ├── security/      # leitura de cookies e cabeçalhos (somente leitura)
    │   └── runners/TestRunner.java
    └── resources/
        ├── features/                   # login, produtos, carrinho, checkout
        ├── features/nao_funcionais/    # performance e segurança
        ├── config.properties
        ├── known-defects.properties    # cenário @defeito → ID do defeito (link no Allure)
        ├── allure.properties
        ├── junit-platform.properties   # glue, plugins, filtro padrão e paralelismo
        └── logback-test.xml
```

### Fluxo de um passo: feature → step → page → ui

Exemplo do cenário `@CT001_Login`:

```gherkin
# features/login.feature
Quando ele se autentica com a senha "secret_sauce"
```

```java
// steps/LoginSteps.java: liga o texto do Gherkin ao código
@Quando("ele se autentica com a senha {string}")
public void authenticatesWithPassword(String password) {
  loginPage.login(context.username(), password);
}
```

```java
// pages/LoginPage.java: ações da página (a espera fica dentro de type/click da BasePage)
public void login(String username, String password) {
  type(USERNAME, username);
  type(PASSWORD, password);
  click(LOGIN_BUTTON);
}
```

```java
// ui/LoginUi.java: onde o elemento está na tela
public static final By USERNAME = DataTest.of("username");        // [data-test='username']
public static final By PASSWORD = DataTest.of("password");
public static final By LOGIN_BUTTON = DataTest.of("login-button");
```

A validação fica no step do `Então`:

```java
// steps/CommonSteps.java
@Então("a página de produtos é exibida")
public void productsPageIsDisplayed() {
  assertThat(productsPage.isLoaded()).as("Página de produtos carregada").isTrue();
  assertThat(header.pageTitle()).as("Título da página").isEqualTo(PageTitles.PRODUCTS);
}
```

---

## 5. Como criar um novo cenário

Exemplo: verificar que o carrinho mostra a quantidade de um produto.

1. **Escreva o cenário** no `.feature` da funcionalidade. Use o próximo ID livre e as tags adequadas:
   ```gherkin
   @CT014_Carrinho @regressivo @positivo
   Cenário: Quantidade do produto no carrinho
     Dado que o carrinho do usuário "standard_user" contém o produto "Sauce Labs Onesie"
     Quando ele acessa o carrinho
     Então a quantidade do produto "Sauce Labs Onesie" é "1"
   ```
2. **Rode só esse cenário** com `./mvnw test -Dcucumber.filter.tags="@CT014_Carrinho"`. O Cucumber avisa quais passos estão `Undefined` e sugere a assinatura do método.
3. **Reaproveite passos existentes** sempre que possível. Busque o texto em `steps/` (`Dado`/`Quando`/`Então`).
4. **Locator:** se o elemento ainda não estiver mapeado, inspecione a página (F12) e adicione na classe `ui/` da tela. Use `[data-test='...']` sempre que existir:
   ```java
   public static final By ITEM_QUANTITIES = DataTest.of("item-quantity");
   ```
5. **Page Object:** crie um método de ação ou de leitura em `pages/`, usando os helpers da `BasePage` (`click`, `type`, `textOf`, `textsOf`, `isDisplayed`...). Não use `findElement` direto e não faça asserção aqui.
6. **Step:** implemente o passo em `steps/`, chamando a page e validando com AssertJ (`assertThat(...).as("descrição")...`).
7. **Rode de novo**, depois rode a regressão (`./mvnw clean test`) e confira a formatação com `./mvnw spotless:check`.

---

## 6. Convenções do projeto

- **Locators só em `ui/`**, como `public static final By`. A ordem de preferência é `data-test` → `id` → `name` → CSS → XPath.
- **Nada de** `@FindBy`/`PageFactory`, `WebElement` guardado em campo, `Thread.sleep`, implicit wait ou métodos `waitForElement*`.
- **Esperas explícitas só dentro da `BasePage`.** Cada ação já espera o elemento ficar pronto.
- **Pages** têm só ações e leitura de estado, **sem asserções**.
- **Steps** só orquestram pages e fazem as **asserções** com AssertJ. Sem `findElement`, locators ou waits. Tabelas usam `SoftAssertions`, para mostrar todas as diferenças de uma vez.
- **Sem estado estático entre cenários.** O estado do cenário vai em `context/`, injetado pelo PicoContainer, que cria uma instância nova por cenário. O driver fica em `ThreadLocal`.
- **Cenários independentes.** Cada um abre o próprio navegador anônimo e monta as próprias pré-condições.
- **Não mude um resultado esperado só para o teste passar.** Se o site se comportar diferente, o teste deve falhar e a divergência deve ser reportada (veja [Defeitos conhecidos](#10-defeitos-conhecidos)).
- **Formatação** com google-java-format (Spotless): `./mvnw spotless:check` valida e `./mvnw spotless:apply` corrige.

---

## 7. Tags

| Tag | Significado |
|---|---|
| `@login`, `@produtos`, `@carrinho`, `@checkout` | funcionalidade (vira **Feature** no Allure) |
| `@CTnnn_<Funcionalidade>` | ID de rastreabilidade do cenário (ex.: `@CT001_Login`) |
| `@regressivo` | faz parte da regressão funcional |
| `@smoke` | caminho crítico, rápido; roda primeiro no CI |
| `@positivo` / `@negativo` | fluxo feliz / validação de erro |
| `@ordenacao`, `@detalhe`, `@menu`, `@calculo`, `@cancelamento`, `@validacao` | agrupamentos por assunto |
| `@seguranca` | verificação de segurança (funcional no login ou passiva) |
| `@passivo` | verificação de segurança somente leitura (`seguranca.feature`) |
| `@performance` | performance percebida; **fora** do filtro padrão |
| `@defeito` | defeito conhecido; **fora** do filtro padrão, com job próprio no CI |

O filtro padrão, configurado em `junit-platform.properties`, é `not @defeito and not @performance`.

---

## 8. CI/CD

### `tests.yml`: a cada push/PR na `main`, todo dia às 09:00 (BRT) e sob demanda

```text
smoke (chrome) ──► regressão (chrome) ─┐
               └─► regressão (firefox) ├──► Allure Report ──► GitHub Pages (só na main)
defeitos conhecidos (@defeito) ────────┘
```

| Job | O que faz |
|---|---|
| **smoke** | `@smoke and not @defeito` no Chrome headless. Se falhar, a regressão nem começa. |
| **regression** | `not @defeito and not @performance` em matriz **Chrome** e **Firefox** |
| **known-defects** | só `@defeito`, com `continue-on-error`. Deve ficar vermelho enquanto os defeitos existirem; quando ficar verde, o defeito foi corrigido. |
| **report** | junta os `allure-results` de todos os jobs, recupera o histórico (tendência) do relatório anterior, gera o Allure e publica em <https://lucasspaula0509.github.io/saucedemo-automation-portifolio/>. Em PR, o relatório fica só como artifact. |

- **Execução manual:** em *Actions → Testes E2E → Run workflow*, escolha o `browser` e as `tags`.
- **Artifacts** (sempre enviados, mesmo com falha): `allure-results-*`, `evidencias-*` (`cucumber.html` e `surefire-reports`) e `allure-report`.
- Os screenshots de falha ficam dentro do Allure, anexados ao passo que falhou.

### `lint.yml`: a cada push/PR

- `actionlint` valida os workflows.
- `spotless:check` valida a formatação do Java.

### `security-performance.yml`: só agendado (segunda, 10:00 BRT) ou manual

- Performance (`@performance`), Lighthouse CI, segurança passiva (`@seguranca`) e OWASP ZAP Baseline. Detalhes na próxima seção.

### Jenkins (opcional)

O [`Jenkinsfile`](Jenkinsfile) tem os estágios Smoke → Regressão → Defeitos conhecidos e publica com o plugin Allure Jenkins. O estágio de defeitos marca o build como *UNSTABLE*, sem quebrá-lo. Para usar, cadastre um JDK 21 com o nome `temurin-21` e a ferramenta Allure.

---

## 9. Performance e segurança

O saucedemo é um **site público de terceiros**, e não temos autorização do dono para testes agressivos. Por isso:

- **Não há teste de carga ou stress** (JMeter, k6, Gatling). Muitos usuários virtuais seriam, na prática, um ataque de negação de serviço.
- **Não há scan ativo, fuzzing, força bruta ou injeção.** Só verificações **passivas**: ler o que o navegador já recebe numa navegação normal.

### Performance (`@performance`)

Cada cenário faz **uma única navegação**:

| Cenário | Métrica | Limite (configurável) |
|---|---|---|
| Carregamento da página de login | `loadEventEnd` (Navigation Timing) e **LCP** (`PerformanceObserver`) | 3000 ms / 2500 ms |
| Login do `standard_user` até a página de produtos | tempo do clique até a lista visível | 2000 ms |
| Login do `performance_glitch_user` | o mesmo tempo, que **deve ficar acima** do limite (o usuário simula lentidão) | 2000 ms |

As métricas medidas são anexadas ao Allure em JSON.

O tempo do login é medido pelo próprio teste e inclui a latência do WebDriver, por isso o limite é folgado.

**Lighthouse CI** ([`lighthouserc.json`](lighthouserc.json)):
- faz 3 execuções na página de login (perfil desktop) e usa a execução mediana;
- exige performance ≥ 0.8, acessibilidade ≥ 0.9 e boas práticas ≥ 0.9;
- publica o relatório como artifact `lighthouse-report`.

Para rodar o Lighthouse localmente: `npx @lhci/cli autorun`.

**Último resultado (31/10/2026):**
- performance **0.99** e acessibilidade **1.0**;
- boas práticas **0.81**, abaixo do mínimo de 0.9. O site usa *unload event listeners*, que estão descontinuados no Chrome.

Por isso o job **Lighthouse CI** fica vermelho. Isso é um achado do site, e o limite não foi reduzido.

### Segurança

| Verificação | Onde | Cobre | **Não** cobre |
|---|---|---|---|
| Senha mascarada, acesso direto sem login, sessão após logout | `login.feature` (`@seguranca`) | controles de acesso visíveis ao usuário | autorização no servidor, APIs |
| HTTP redireciona para HTTPS | `seguranca.feature` | redirecionamento do domínio principal | versões/cifras de TLS, certificados |
| Atributos do cookie de sessão (`Secure`, `HttpOnly`, `SameSite`) | `seguranca.feature` | o cookie `session-username` visto pelo navegador | outros cookies, roubo ou fixação de sessão |
| `autocomplete` do campo de senha | `seguranca.feature` | se o atributo existe e tem valor adequado | política de senha do servidor |
| Cabeçalhos CSP, X-Frame-Options, X-Content-Type-Options e HSTS | `seguranca.feature` (via **Chrome DevTools Protocol**; só Chrome/Edge, no Firefox o cenário é pulado) | a resposta do documento principal | conteúdo da CSP, cabeçalhos de outros recursos |
| **OWASP ZAP Baseline** (`zap/rules.tsv`) | `security-performance.yml` | spider de 1 minuto e regras **passivas** do ZAP | qualquer regra ativa (XSS, SQLi etc.), áreas autenticadas |

- **Achados não reprovam os testes por padrão.** Eles vão para o Allure (anexos "achados" e "evidência") e para o log como `WARN`. Com `-Dsecurity.fail.on.findings=true`, cada achado reprova o cenário.
- O ZAP roda com `fail_action: false` e não abre issues. O relatório HTML fica no artifact `zap-report`.
- **Achados atuais:**
  - o cookie de sessão não tem `Secure` nem `HttpOnly` (o Firefox reporta `SameSite=None`);
  - o campo de senha não tem `autocomplete`;
  - a página de login não envia CSP, X-Frame-Options, X-Content-Type-Options nem HSTS.

---

## 10. Defeitos conhecidos

Cenários marcados com `@defeito` **continuam falhando enquanto o defeito existir**. O resultado esperado do cenário não é alterado. Eles ficam fora da regressão padrão e rodam no job `known-defects`.

No Allure, aparecem na categoria **"Defeitos conhecidos"**, com o link do ID configurado em [`known-defects.properties`](src/test/resources/known-defects.properties).

| ID | Cenário | Comportamento esperado | Comportamento observado | Observado em |
|---|---|---|---|---|
| DEF-001 | `@CT013_Carrinho` Reset App State restaura os botões dos produtos | botões voltam para "Add to cart" | o contador some, mas o botão continua "Remove" até recarregar a página | 30/10/2026 |
| DEF-002 | `@CT011_Checkout` Checkout bloqueado com carrinho vazio | o checkout não é iniciado | o site vai para `/checkout-step-one.html` com o carrinho vazio | 30/10/2026 |
| DEF-003 | `@CT009_Produtos` Opções disponíveis no menu lateral | All Items, About, Logout, Reset App State | aparece também **"Dynamic Catalog"** | 31/10/2026 |

Quando um defeito for corrigido:
1. o job `known-defects` passa a mostrar o cenário como aprovado;
2. remova a tag `@defeito` do cenário e a linha do `known-defects.properties`.

---

## 11. Decisões técnicas

| Decisão | Motivo |
|---|---|
| Tags `@regressivo` e `@CTnnn_<Funcionalidade>` adicionadas às features | pedido do responsável pelo projeto, para rastreabilidade. Fora isso, o texto dos cenários originais não foi alterado. |
| `@defeito` adicionado ao `@CT009_Produtos` (DEF-003) | decisão do responsável pelo projeto, depois que o site passou a exibir "Dynamic Catalog". O resultado esperado foi mantido. |
| IDs de defeito em `known-defects.properties` | liga cada cenário `@defeito` a um link de issue sem editar as features |
| `AllureResultEnricher` (`TestLifecycleListener` via SPI) | ajusta o resultado antes de gravar: Feature pela tag, link do defeito, prefixo `[Defeito conhecido DEF-xxx]` (as categorias do Allure só filtram por status e mensagem) e o parâmetro "Navegador" (para Chrome e Firefox não virarem retentativas no relatório consolidado) |
| Cucumber 7 e JUnit 5, mesmo havendo versões mais novas | o `allure-cucumber7-jvm` exige Cucumber 7 |
| Sem AspectJ | anexos e passos usam a API do Allure e do Cucumber (`Allure.addAttachment`, `scenario.attach`), sem agente na JVM |
| Selenium Manager em vez de WebDriverManager | já vem no Selenium 4 e baixa o driver certo para o navegador instalado |
| Maven Toolchains num profile ativado por `~/.m2/toolchains.xml` | garante o JDK 21 mesmo com outro `JAVA_HOME`; sem o arquivo (CI, Jenkins), usa o `JAVA_HOME` |
| `CI=true` força headless | o GitHub Actions define essa variável; não é preciso lembrar do `-Dheadless` |
| google-java-format 1.36.0 | a 1.37.0 falha no Spotless 3.10.4. O formatador exige que o **Maven rode em JDK 21**. |
| Um único passo "a mensagem {string} é exibida" | lê a mensagem de erro (`data-test='error'`) ou a de confirmação do pedido (`complete-header`), o que estiver na tela |
| Botões acionados pelo rótulo do Gherkin ("Back Home", "Finish"...) | um mapa em `ActionButtonsUi` liga o texto ao locator, e um rótulo desconhecido falha com mensagem clara |
| Rótulos do resumo do checkout sem ":" | a tabela do cenário usa "Payment Information"; a tela mostra "Payment Information:" |
| Screenshot no `@AfterStep`; URL, HTML e console no `@After` | o print mostra a tela no momento exato da falha; o resto é contexto para depuração |
| "que o visitante não está autenticado" não executa nada | cada cenário já começa num navegador anônimo novo, sem sessão |
| Pré-condições de checkout usam "Sauce Labs Backpack" | os cenários que só pedem "carrinho com produto" precisam de algum item; o resumo usa os valores do cenário |
| "preencheu os dados" preenche o formulário; "visualiza o resumo" clica em Continue | mantém o `Dado` como preparação e o `Quando` como ação |
| Valores do checkout comparados como texto e também pela regra de cálculo | o texto confere o que o cliente vê; a regra (8% de imposto, `BigDecimal` HALF_UP) é anexada ao Allure |
| Cabeçalhos lidos pelo CDP usando o nome do comando (`Network.enable`) | as classes `devtools.v1xx` mudam a cada versão do Chrome; o nome do comando continua funcionando quando o Chrome do CI é atualizado |
| Lighthouse com perfil desktop | o site é usado em desktop nos cenários (janela de 1920×1080) |

---

## 12. Troubleshooting

| Problema | Causa / solução |
|---|---|
| **Popup "Alterar sua senha" / "senha vazada" do Chrome** atrapalha o clique | o projeto já abre o Chrome/Edge em modo anônimo e desliga o gerenciador de senhas e o alerta de vazamento (`credentials_enable_service`, `password_manager_enabled`, `password_manager_leak_detection`). Se ainda aparecer, atualize o Chrome e confira se essas opções continuam em `DriverFactory`. |
| **`SessionNotCreatedException` / driver não encontrado** | o Selenium Manager baixa o driver na primeira execução e precisa de internet. Atrás de proxy, configure `HTTPS_PROXY`. Confirme também que o navegador escolhido está instalado. Para usar um driver próprio, passe `-Dwebdriver.chrome.driver=/caminho/chromedriver`. |
| **Timeout com `performance_glitch_user`** | esse usuário tem lentidão proposital (~5 s no login). O `timeout.seconds=10` já cobre esse caso; em máquina ou rede lenta, aumente com `-Dtimeout.seconds=20`. |
| **`release version 21 not supported`** | o Maven está rodando em JDK 17 ou anterior. Ajuste o `JAVA_HOME` para o JDK 21 ou crie o `~/.m2/toolchains.xml` (seção 2). |
| **`spotless:check` falha com "You are running Spotless on JVM 17"** | o formatador precisa do Maven rodando em JDK 21 (o toolchain não vale para o Spotless). Rode com `JAVA_HOME` no JDK 21. |
| **Cenário de cabeçalhos aparece como "skipped" no Firefox** | esperado: a leitura de cabeçalhos usa o Chrome DevTools Protocol, disponível só em Chrome/Edge |
| **O job "Defeitos conhecidos" está vermelho** | esperado enquanto os defeitos existirem; o workflow continua verde (`continue-on-error`) |
| **`./mvnw: Permission denied`** (Linux/macOS) | `chmod +x mvnw` |
