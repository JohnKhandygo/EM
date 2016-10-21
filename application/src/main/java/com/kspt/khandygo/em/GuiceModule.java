package com.kspt.khandygo.em;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kspt.khandygo.em.dao.AuthDAO;
import com.kspt.khandygo.em.dao.AwardsDAO.AwardEntity;
import com.kspt.khandygo.em.dao.OutOfOfficesDAO.OutOfOfficeEntity;
import com.kspt.khandygo.em.dao.UserEntity;
import com.kspt.khandygo.em.dao.VocationsDAO.VocationEntity;
import com.kspt.khandygo.em.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import javax.inject.Provider;
import javax.inject.Singleton;

@Slf4j
public class GuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(EbeanServer.class).toProvider(EbeanProvider.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  private AuthService provideAuthService(final AuthDAO authDAO) {
    return new AuthService(Maps.newConcurrentMap(), authDAO);
  }

  private static class EbeanProvider implements Provider<EbeanServer> {

    @Override
    public EbeanServer get() {
      final Stopwatch started = Stopwatch.createStarted();
      final EbeanServer server = EbeanServerFactory.create(getServerConfig());
      checkNotNull(server, "Cannot create ebean server.");
      log.warn("Elapsed time to create ebean server {}", started);
      return server;
    }

    @NotNull
    private ServerConfig getServerConfig() {
      ServerConfig c = new ServerConfig();
      c.setName("default");
      c.setDefaultServer(true);
      c.setDataSourceConfig(getDataSourceConfig());
      registerClasses(c);
      return c;
    }

    private void registerClasses(final ServerConfig c) {
      c.addClass(UserEntity.class);
      c.addClass(AwardEntity.class);
      c.addClass(OutOfOfficeEntity.class);
      c.addClass(OutOfOfficeEntity.class);
      c.addClass(VocationEntity.class);
    }

    @NotNull
    private DataSourceConfig getDataSourceConfig() {
      final DataSourceConfig dsc = new DataSourceConfig();
      dsc.setUrl("jdbc:mysql://127.0.0.1:3307/test?trustServerCertificate=true");
      dsc.setDriver("org.mariadb.jdbc.Driver");
      dsc.setUsername("em_admin");
      dsc.setPassword("1234");
      return dsc;
    }
  }
}
