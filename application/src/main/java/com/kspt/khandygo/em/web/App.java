package com.kspt.khandygo.em.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import com.kspt.khandygo.em.GuiceModule;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import static org.eclipse.jetty.servlets.CrossOriginFilter.*;
import org.glassfish.jersey.server.validation.ValidationFeature;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class App extends Application<App.AppConfiguration> {

  @Override
  public String getName() {
    return "Employee Management Application";
  }

  @Override
  public void run(final AppConfiguration configuration, final Environment environment)
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

  protected void setupJersey(final Environment environment) {
    environment.jersey().packages("com.kspt.khandygo.em.web.resources");
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
  public void initialize(final Bootstrap<AppConfiguration> bootstrap) {
    final GuiceBundle<AppConfiguration> guiceBundle = GuiceBundle.<AppConfiguration>newBuilder()
        .addModule(new GuiceModule())
        .setConfigClass(AppConfiguration.class)
        .build(Stage.PRODUCTION);
    bootstrap.addBundle(guiceBundle);
  }

  public static void main(final String[] args)
  throws Exception {
    new App().run(args);
  }

  static class AppConfiguration extends Configuration {
  }
}
