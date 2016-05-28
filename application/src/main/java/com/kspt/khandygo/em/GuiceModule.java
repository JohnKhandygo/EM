package com.kspt.khandygo.em;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.kspt.khandygo.em.dao.AuthDAO;
import com.kspt.khandygo.em.services.AuthService;
import com.kspt.khandygo.news.web.api.NewsApi;
import com.kspt.khandygo.persistence.SQLServer;
import com.typesafe.config.Config;
import static com.typesafe.config.ConfigFactory.parseResources;
import com.typesafe.config.ConfigParseOptions;
import feign.Client.Default;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jaxrs.JAXRSContract;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import lombok.extern.slf4j.Slf4j;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Slf4j
public class GuiceModule extends AbstractModule {

  @Override
  protected void configure() {

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

  @Provides
  @Singleton
  private SQLServer provideSQLServer(final Config config) {
    final Config databaseConfig = config.getConfig("database");
    return SQLServer.newMySQLServer(
        databaseConfig.getString("host"),
        databaseConfig.getString("port"),
        databaseConfig.getString("scheme"),
        databaseConfig.getString("user"),
        databaseConfig.getString("password"));
  }

  @Provides
  @Singleton
  private AuthService provideAuthService(final AuthDAO authDAO) {
    return new AuthService(Maps.newConcurrentMap(), authDAO);
  }

  @Provides
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
  }

  private static SSLContext sslContext() {
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
  }
}
