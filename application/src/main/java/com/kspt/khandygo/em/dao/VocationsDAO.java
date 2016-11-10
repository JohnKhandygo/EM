package com.kspt.khandygo.em.dao;

import com.avaje.ebean.EbeanServer;
import static com.avaje.ebean.Expr.*;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.kspt.khandygo.em.core.Vocation;
import com.kspt.khandygo.em.utils.Tuple2;
import static java.util.stream.Collectors.toList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class VocationsDAO {

  private final EbeanServer ebean;

  public int save(final @NonNull Vocation vocation) {
    final VocationEntity vocationEntity = VocationEntity.newOne(vocation);
    Verify.verify(vocationEntity.id == null, "Cannot save entity with non null id.");
    ebean.save(vocationEntity);
    Verify.verifyNotNull(vocationEntity.id, "Id must be non null after save.");
    return vocationEntity.id;
  }

  @NonNull
  public Vocation get(final int id) {
    final VocationEntity found = ebean.find(VocationEntity.class).where().eq("id", id).findUnique();
    Verify.verifyNotNull(found, "Cannot find vocation entity for id %s.", id);
    return found.toVocation();
  }

  @NonNull
  public Vocation update(final int id, final @NonNull Vocation model) {
    final VocationEntity vocationEntity = VocationEntity.existedOne(id, model);
    Verify.verifyNotNull(vocationEntity.id, "Cannot update entity with null id.");
    ebean.update(vocationEntity);
    return vocationEntity.toVocation();
  }

  @NonNull
  public List<Tuple2<Integer, Vocation>> approvedFor(final int employeeId) {
    final List<VocationEntity> vocationEntities = ebean.find(VocationEntity.class)
        .where()
        .and(eq("employee_id", employeeId), eq("approved", 1))
        .findList();
    return vocationEntities.stream()
        .map(entity -> Tuple2.of(entity.id, entity.toVocation()))
        .collect(toList());
  }

  @NonNull
  public List<Tuple2<Integer, Vocation>> pendingFor(final int employeeId) {
    final List<VocationEntity> awardEntities = ebean.find(VocationEntity.class).where()
        .and(
            eq("employee_id", employeeId),
            and(
                eq("approved", 0),
                and(
                    eq("rejected", 0),
                    eq("cancelled", 0))))
        .findList();
    return awardEntities.stream()
        .map(entity -> Tuple2.of(entity.id, entity.toVocation()))
        .collect(toList());
  }

  @Entity
  @Table(name = "vocations")
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @EqualsAndHashCode
  @ToString
  @SuppressWarnings("WeakerAccess")
  public static class VocationEntity {
    @Id
    private final Integer id;

    @ManyToOne
    private final UserEntity employee;

    private final Long timestamp;

    private final Long duration;

    private final Boolean approved;

    private final Boolean rejected;

    private final Boolean cancelled;

    @NonNull
    static VocationEntity newOne(final @NonNull Vocation vocation) {
      Preconditions.checkState(vocation.employee() instanceof UserEntity,
          "There is no sufficient type information to save %s.", vocation);
      return new VocationEntity(
          null,
          (UserEntity) vocation.employee(),
          vocation.when(),
          vocation.duration(),
          vocation.approved(),
          vocation.rejected(),
          vocation.cancelled());
    }

    @NonNull
    static VocationEntity existedOne(final int id, final @NonNull Vocation vocation) {
      Preconditions.checkState(vocation.employee() instanceof UserEntity,
          "There is no sufficient type information to save %s.", vocation);
      return new VocationEntity(
          id,
          (UserEntity) vocation.employee(),
          vocation.when(),
          vocation.duration(),
          vocation.approved(),
          vocation.rejected(),
          vocation.cancelled());
    }

    @NonNull
    Vocation toVocation() {
      return new Vocation(employee, timestamp, duration, approved, rejected, cancelled);
    }
  }
}
