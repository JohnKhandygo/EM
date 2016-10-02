package com.kspt.khandygo.em.dao;

import com.avaje.ebean.EbeanServer;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.kspt.khandygo.em.core.Award;
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
public class AwardsDAO {

  private final EbeanServer ebean;

  public List<Tuple2<Integer, Award>> approvedFor(final int employeeId) {
    final List<AwardEntity> awardEntities = ebean.find(AwardEntity.class).where()
        .eq("employee_id", employeeId)
        .and()
        .eq("approved", 1)
        .findList();
    return awardEntities.stream()
        .map(awardEntity -> Tuple2.of(awardEntity.id, awardEntity.toAward()))
        .collect(toList());
  }

  @NonNull
  public List<Tuple2<Integer, Award>> pendingFor(final int employeeId) {
    final List<AwardEntity> awardEntities = ebean.find(AwardEntity.class).where()
        .eq("employee_id", employeeId)
        .and()
        .eq("approved", 0)
        .and()
        .eq("rejected", 0)
        .and()
        .eq("cancelled", 0)
        .findList();
    return awardEntities.stream()
        .map(awardEntity -> Tuple2.of(awardEntity.id, awardEntity.toAward()))
        .collect(toList());
  }

  public int save(final @NonNull Award award) {
    final AwardEntity awardEntity = AwardEntity.newOne(award);
    Verify.verify(awardEntity.id == null, "Cannot save entity with non null id.");
    ebean.save(awardEntity);
    Verify.verifyNotNull(awardEntity.id, "Id must be non null after save.");
    return awardEntity.id;
  }

  @NonNull
  public Award get(final int id) {
    final AwardEntity found = ebean.find(AwardEntity.class).where().eq("id", id).findUnique();
    Verify.verifyNotNull(found, "Cannot find award entity for id %s.", id);
    return found.toAward();
  }

  @NonNull
  public Award update(final int id, final @NonNull Award model) {
    final AwardEntity awardEntity = AwardEntity.existedOne(id, model);
    Verify.verifyNotNull(awardEntity.id, "Cannot update entity with null id.");
    ebean.update(awardEntity);
    return awardEntity.toAward();
  }

  @Entity
  @Table(name = "awards")
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @EqualsAndHashCode
  @ToString
  @SuppressWarnings("WeakerAccess")
  public static class AwardEntity {
    @Id
    private final Integer id;

    @ManyToOne
    private final UserEntity employee;

    private final Long timestamp;

    private final Long amount;

    private final Boolean approved;

    private final Boolean rejected;

    private final Boolean cancelled;

    Award toAward() {
      return new Award(employee, timestamp, amount, approved, rejected, cancelled);
    }

    @NonNull
    static AwardEntity newOne(final Award award) {
      Preconditions.checkState(award.employee() instanceof UserEntity,
          "There is no sufficient type information to save %s.", award);
      return new AwardEntity(
          null,
          (UserEntity) award.employee(),
          award.when(),
          award.amount(),
          award.approved(),
          award.rejected(),
          award.cancelled());
    }

    static AwardEntity existedOne(final int id, final Award award) {
      Preconditions.checkState(award.employee() instanceof UserEntity,
          "There is no sufficient type information to save %s.", award);
      return new AwardEntity(
          id,
          (UserEntity) award.employee(),
          award.when(),
          award.amount(),
          award.approved(),
          award.rejected(),
          award.cancelled());
    }
  }
}
