package com.kspt.khandygo.em.dao;

import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.core.OutOfOffice;
import com.kspt.khandygo.em.utils.Tuple2;
import com.kspt.khandygo.persistence.Gateway;
import static java.util.stream.Collectors.toList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class OutOfOfficesDAO {

  private final Gateway gateway;

  public int save(final OutOfOffice outOfOffice) {
    final OutOfOfficeEntity outOfOfficeEntity = OutOfOfficeEntity.newOne(outOfOffice);
    return gateway.save(outOfOfficeEntity).id;
  }

  public OutOfOffice get(final int id) {
    return gateway.find(OutOfOfficeEntity.class).where().eq("id", id).unique().toOuOfOffice();
  }

  public OutOfOffice update(final int id, final OutOfOffice model) {
    final OutOfOfficeEntity outOfOfficeEntity = OutOfOfficeEntity.existedOne(id, model);
    return gateway.update(outOfOfficeEntity).toOuOfOffice();
  }

  public List<Tuple2<Integer, OutOfOffice>> approvedFor(final int employeeId) {
    final List<OutOfOfficeEntity> outOfOfficeEntities = gateway.find(OutOfOfficeEntity.class)
        .where()
        .eq("employee_id", employeeId)
        .and()
        .eq("cancelled", 0)
        .list();
    return outOfOfficeEntities.stream()
        .map(entity -> new Tuple2<>(entity.id, entity.toOuOfOffice()))
        .collect(toList());
  }

  @Entity
  @Table(name = "out_of_offices")
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @EqualsAndHashCode
  @ToString
  private static class OutOfOfficeEntity {
    @Id
    private final Integer id;

    private final UserEntity employee;

    private final Long timestamp;

    private final Long duration;

    private final String reason;

    private final Boolean cancelled;

    OutOfOffice toOuOfOffice() {
      return new OutOfOffice(employee, timestamp, duration, reason, cancelled);
    }

    static OutOfOfficeEntity newOne(final OutOfOffice outOfOffice) {
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

    static OutOfOfficeEntity existedOne(final int id, final OutOfOffice outOfOffice) {
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
