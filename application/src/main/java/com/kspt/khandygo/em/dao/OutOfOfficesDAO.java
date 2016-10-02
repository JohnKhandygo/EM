package com.kspt.khandygo.em.dao;

import com.avaje.ebean.EbeanServer;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.kspt.khandygo.em.core.OutOfOffice;
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
public class OutOfOfficesDAO {

  private final EbeanServer ebean;

  public int save(final @NonNull OutOfOffice outOfOffice) {
    final OutOfOfficeEntity outOfOfficeEntity = OutOfOfficeEntity.newOne(outOfOffice);
    Verify.verify(outOfOfficeEntity.id == null, "Cannot save entity with non null id.");
    ebean.save(outOfOfficeEntity);
    Verify.verifyNotNull(outOfOfficeEntity.id, "Id must be non null after save.");
    return outOfOfficeEntity.id;
  }

  @NonNull
  public OutOfOffice get(final int id) {
    final OutOfOfficeEntity found = ebean.find(OutOfOfficeEntity.class)
        .where()
        .eq("id", id)
        .findUnique();
    Verify.verifyNotNull(found, "Cannot find out of office entity for id %s.", id);
    return found.toOuOfOffice();
  }

  @NonNull
  public OutOfOffice update(final int id, final @NonNull OutOfOffice model) {
    final OutOfOfficeEntity outOfOfficeEntity = OutOfOfficeEntity.existedOne(id, model);
    Verify.verifyNotNull(outOfOfficeEntity.id, "Cannot update entity with null id.");
    ebean.update(outOfOfficeEntity);
    return outOfOfficeEntity.toOuOfOffice();
  }

  @NonNull
  public List<Tuple2<Integer, OutOfOffice>> approvedFor(final int employeeId) {
    final List<OutOfOfficeEntity> outOfOfficeEntities = ebean.find(OutOfOfficeEntity.class)
        .where()
        .eq("employee_id", employeeId)
        .and()
        .eq("cancelled", 0)
        .findList();
    return outOfOfficeEntities.stream()
        .map(entity -> Tuple2.of(entity.id, entity.toOuOfOffice()))
        .collect(toList());
  }

  @Entity
  @Table(name = "out_of_offices")
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @EqualsAndHashCode
  @ToString
  @SuppressWarnings("WeakerAccess")
  public static class OutOfOfficeEntity {
    @Id
    private final Integer id;

    @ManyToOne
    private final UserEntity employee;

    private final Long timestamp;

    private final Long duration;

    private final String reason;

    private final Boolean cancelled;

    @NonNull
    OutOfOffice toOuOfOffice() {
      return new OutOfOffice(employee, timestamp, duration, reason, cancelled);
    }

    @NonNull
    static OutOfOfficeEntity newOne(final @NonNull OutOfOffice outOfOffice) {
      Preconditions.checkState(outOfOffice.employee() instanceof UserEntity,
          "There is no sufficient type information to save %s.", outOfOffice);
      return new OutOfOfficeEntity(
          null,
          (UserEntity) outOfOffice.employee(),
          outOfOffice.when(),
          outOfOffice.duration(),
          outOfOffice.reason(),
          outOfOffice.cancelled());
    }

    @NonNull
    static OutOfOfficeEntity existedOne(final int id, final @NonNull OutOfOffice outOfOffice) {
      Preconditions.checkState(outOfOffice.employee() instanceof UserEntity,
          "There is no sufficient type information to save %s.", outOfOffice);
      return new OutOfOfficeEntity(
          id,
          (UserEntity) outOfOffice.employee(),
          outOfOffice.when(),
          outOfOffice.duration(),
          outOfOffice.reason(),
          outOfOffice.cancelled());
    }
  }
}
