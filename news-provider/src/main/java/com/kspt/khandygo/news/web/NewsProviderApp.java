package com.kspt.khandygo.news.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import com.kspt.khandygo.news.web.NewsProviderApp.NewsProviderConfiguration;
import com.kspt.khandygo.persistence.SQLServer;
import com.typesafe.config.Config;
import static com.typesafe.config.ConfigFactory.parseResources;
import com.typesafe.config.ConfigParseOptions;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import static java.lang.Thread.currentThread;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import static org.eclipse.jetty.servlets.CrossOriginFilter.*;
import org.glassfish.jersey.server.validation.ValidationFeature;
import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.File;
import java.util.EnumSet;

public class NewsProviderApp extends Application<NewsProviderConfiguration> {

  @Override
  public String getName() {
    return "News Provider Service";
  }

  @Override
  public void run(final NewsProviderConfiguration configuration, final Environment environment)
  throws Exception {
    setupCORSFilter(environment);
    setupJersey(environment);
    setupJacksonMapper(environment);
  }

  private void setupCORSFilter(final Environment environment) {
    FilterRegistration.Dynamic filter = environment.servlets()
        .addFilter("CORSFilter", CrossOriginFilter.class);
    filter.addMappingForUrlPatterns(
        EnumSet.of(DispatcherType.REQUEST), false,
        environment.getApplicationContext().getContextPath() + "*");
    filter.setInitParameter(ALLOWED_METHODS_PARAM, "GET,PUT,POST,OPTIONS,DELETE");
    filter.setInitParameter(ALLOWED_HEADERS_PARAM, "Origin, Content-Type, Accept, session_id");
    filter.setInitParameter(ALLOWED_ORIGINS_PARAM, "null,localhost,http://localhost:*");
    filter.setInitParameter(ALLOW_CREDENTIALS_PARAM, "true");
  }

  private void setupJersey(final Environment environment) {
    environment.jersey().packages("com.kspt.khandygo.news.web.resources");
    environment.jersey().enable(ValidationFeature.class.getName());
  }

  private void setupJacksonMapper(final Environment environment) {
    final ObjectMapper mapper = environment.getObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    mapper.findAndRegisterModules();
  }

  @Override
  public void initialize(final Bootstrap<NewsProviderConfiguration> bootstrap) {
    final GuiceBundle<NewsProviderConfiguration> guiceBundle = GuiceBundle
        .<NewsProviderConfiguration>newBuilder()
        .addModule(new GuiceModule())
        .setConfigClass(NewsProviderConfiguration.class)
        .build(Stage.PRODUCTION);
    bootstrap.addBundle(guiceBundle);
  }

  public static void main(final String[] args)
  throws Exception {
    new NewsProviderApp().run(args);
  }

  static class NewsProviderConfiguration extends Configuration {
  }

  @Slf4j
  private static class GuiceModule extends AbstractModule {

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
  }
}
