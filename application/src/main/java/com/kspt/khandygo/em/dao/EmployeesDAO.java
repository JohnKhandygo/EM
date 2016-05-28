package com.kspt.khandygo.em.dao;

import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.utils.Tuple2;
import com.kspt.khandygo.persistence.Gateway;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class EmployeesDAO {

  private final Gateway gateway;

  public Employee get(final int id) {
    return gateway.find(UserEntity.class).where().eq("id", id).unique();
  }

  public List<Tuple2<Integer, ? extends Employee>> getAllUnderThePatronageOf(
      final Employee manager) {
    Preconditions.checkState(manager instanceof UserEntity);
    final Integer managerId = ((UserEntity) manager).id();
    return gateway.find(UserEntity.class)
        .where()
        .eq("manager_id", managerId)
        .list()
        .stream()
        .map(entity -> new Tuple2<>(entity.id(), entity))
        .collect(toList());
  }

  public List<Tuple2<Integer, ? extends Employee>> getAllUnderThePatronageOf(
      final int managerId) {
    return gateway.find(UserEntity.class)
        .where()
        .eq("manager_id", managerId)
        .list()
        .stream()
        .map(entity -> new Tuple2<>(entity.id(), entity))
        .collect(toList());
  }

  public List<Tuple2<Integer, ? extends Employee>> getAllMasteredBy(final int paymasterId) {
    return gateway.find(UserEntity.class)
        .where()
        .eq("paymaster_id", paymasterId)
        .list()
        .stream()
        .map(entity -> new Tuple2<>(entity.id(), entity))
        .collect(toList());
  }
}
