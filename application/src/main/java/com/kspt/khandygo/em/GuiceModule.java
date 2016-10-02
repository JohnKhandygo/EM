package com.kspt.khandygo.em;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kspt.khandygo.em.dao.AuthDAO;
import com.kspt.khandygo.em.services.AuthService;
import com.typesafe.config.Config;
import static com.typesafe.config.ConfigFactory.parseResources;
import com.typesafe.config.ConfigParseOptions;
import static java.lang.Thread.currentThread;
import static java.util.stream.Collectors.joining;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class GuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(EbeanServer.class).toProvider(EbeanProvider.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  private Config provideConfiguration() {
    final ConfigParseOptions options = ConfigParseOptions.defaults().setAllowMissing(false);
    final ClassLoader classLoader = currentThread().getContextClassLoader();
    final Config c = parseResources(classLoader, "app.conf", options);
    final File folder = new File(c.origin().filename()).getParentFile();
    log.warn("Loading configuration file from {}.", folder.getAbsolutePath());
    return c.resolve();
  }

  /*@Provides
  @Singleton
  private SQLServer provideSQLServer(final Config config) {
    final Config databaseConfig = config.getConfig("database");
    return SQLServer.newMySQLServer(
        databaseConfig.getString("host"),
        databaseConfig.getString("port"),
        databaseConfig.getString("scheme"),
        databaseConfig.getString("user"),
        databaseConfig.getString("password"));
  }*/

  @Provides
  @Singleton
  private AuthService provideAuthService(final AuthDAO authDAO) {
    return new AuthService(Maps.newConcurrentMap(), authDAO);
  }

  /*@Provides
  @Singleton
  private NewsApi provideNewsApi(final Config config) {
    final Config newsConfig = config.getConfig("news");
    final String apiAddress = format("%s://%s:%s",
        newsConfig.getString("scheme"),
        newsConfig.getString("host"),
        newsConfig.getString("port"));
    final ObjectMapper dodgyMapper = new ObjectMapper()
        .registerModule(new ParameterNamesModule(Mode.PROPERTIES));
    return Feign.builder()
        .client(new Default(sslContext().getSocketFactory(), allowAllHostnameVerifier()))
        .decoder(new JacksonDecoder(dodgyMapper))
        .contract(new JAXRSContract())
        .target(NewsApi.class, apiAddress);
  }*/

  /*private static SSLContext sslContext() {
    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
      public void checkClientTrusted(X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(X509Certificate[] certs, String authType) {
      }

      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }
    }
    };

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new SecureRandom());
      return sc;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new IllegalStateException(e);
    }
  }

  private static HostnameVerifier allowAllHostnameVerifier() {
    return (s, sslSession) -> true;
  }*/

  private static class EbeanProvider implements Provider<EbeanServer> {

    private static final String EBEAN_PROPERTIES = "ebean.properties";

    @Override
    public EbeanServer get() {
      final Stopwatch started = Stopwatch.createStarted();
      final EbeanServer server = Ebean.getServer(null);
      checkNotNull(server, "Cannot create ebean server: no '%s' resource file", EBEAN_PROPERTIES);
      try {
        logEbeanProperties();
      } catch (Exception e) {
        log.error("Unexpected error during ebean configuration logging.", e);
      }
      log.warn("Elapsed time to create ebean server {}", started);
      return server;
    }

    private void logEbeanProperties()
    throws URISyntaxException, IOException {
      final URL propertiesUrl = currentThread().getContextClassLoader()
          .getResource(EBEAN_PROPERTIES);
      checkNotNull(propertiesUrl, "Cannnot resolve resource name '%s'.", EBEAN_PROPERTIES);
      final URI propertiesUri = propertiesUrl.toURI();
      final String properties = Files.lines(Paths.get(propertiesUri)).collect(joining("\n"));
      log.warn("Loading '{}' from {}:\n{}", EBEAN_PROPERTIES, propertiesUri.getPath(), properties);
      log.warn("End of '{}'.", EBEAN_PROPERTIES);
    }
  }
}
