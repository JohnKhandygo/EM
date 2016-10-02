package com.kspt.khandygo.em.dao;

import com.avaje.ebean.EbeanServer;
import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.utils.Tuple2;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class EmployeesDAO {

  private final EbeanServer ebean;

  @NonNull
  public Employee get(final int id) {
    return ebean.find(UserEntity.class).where().eq("id", id).findUnique();
  }

  @NonNull
  public List<Tuple2<Integer, ? extends Employee>> getAllUnderThePatronageOf(
      final @NonNull Employee manager) {
    Preconditions.checkState(manager instanceof UserEntity);
    final Integer managerId = ((UserEntity) manager).id();
    return ebean.find(UserEntity.class)
        .where()
        .eq("manager_id", managerId)
        .findList()
        .stream()
        .map(entity -> Tuple2.of(entity.id(), entity))
        .collect(toList());
  }

  @NonNull
  public List<Tuple2<Integer, ? extends Employee>> getAllUnderThePatronageOf(
      final int managerId) {
    return ebean.find(UserEntity.class)
        .where()
        .eq("manager_id", managerId)
        .findList()
        .stream()
        .map(entity -> Tuple2.of(entity.id(), entity))
        .collect(toList());
  }

  @NonNull
  public List<Tuple2<Integer, ? extends Employee>> getAllMasteredBy(final int paymasterId) {
    return ebean.find(UserEntity.class)
        .where()
        .eq("paymaster_id", paymasterId)
        .findList()
        .stream()
        .map(entity -> Tuple2.of(entity.id(), entity))
        .collect(toList());
  }
}
