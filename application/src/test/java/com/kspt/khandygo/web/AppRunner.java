package com.kspt.khandygo.web;

import com.google.common.io.Resources;
import com.kspt.khandygo.em.web.App;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppRunner {

  public static void main(String[] args)
  throws Exception {
    setupSwagger();
    try {
      final String configFile = Resources.getResource("app.yml").getFile();
      new TestApp().run("server", configFile);
    } catch (Exception e) {
      log.error("Error occurred", e);
      throw new RuntimeException(e);
    }
  }

  private static void setupSwagger() {
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setVersion("1.1");
    beanConfig.setSchemes(new String[]{"https"});
    beanConfig.setHost("localhost:8443");
    beanConfig.setBasePath("/api");
    beanConfig.setResourcePackage("com.kspt.khandygo.web.resources");
    beanConfig.setScan(true);
  }

  private static class TestApp extends App {
    @Override
    protected void setupJersey(final Environment environment) {
      super.setupJersey(environment);
      environment.jersey().register(ApiListingResource.class);
      environment.jersey().register(SwaggerSerializers.class);
    }
  }
}