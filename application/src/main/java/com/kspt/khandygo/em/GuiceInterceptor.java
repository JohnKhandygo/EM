package com.kspt.khandygo.em;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
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
