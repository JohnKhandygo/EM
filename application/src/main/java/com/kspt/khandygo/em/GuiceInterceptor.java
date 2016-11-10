package com.kspt.khandygo.em;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class GuiceInterceptor {

  private final Injector injector = Guice.createInjector(new GuiceModule());

  @AroundInvoke
  public Object injectByGuice(InvocationContext ctx) throws Exception {
    injector.injectMembers(ctx.getTarget());
    return ctx.proceed();
  }
}
