package com.kspt.khandygo.em.web;

import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kspt.khandygo.em.GuiceModule;
import com.kspt.khandygo.em.web.resources.AuthResource;
import com.kspt.khandygo.em.web.resources.AwardsResource;
import com.kspt.khandygo.em.web.resources.EmployeesResource;
import com.kspt.khandygo.em.web.resources.OutOfOfficesResource;
import com.kspt.khandygo.em.web.resources.VocationsResource;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/api")
public class App extends Application {

  private final Set<Object> singletons;

  public App() {
    final Injector injector = Guice.createInjector(new GuiceModule());
    singletons = Sets.newHashSet(
        injector.getInstance(AuthResource.class),
        injector.getInstance(AwardsResource.class),
        injector.getInstance(EmployeesResource.class),
        injector.getInstance(OutOfOfficesResource.class),
        injector.getInstance(VocationsResource.class));
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }
}
