package com.kspt.khandygo.em.dao;

import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.core.Vocation;
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
public class VocationsDAO {

  private final Gateway gateway;

  public int save(final Vocation vocation) {
    final VocationEntity vocationEntity = VocationEntity.newOne(vocation);
    return gateway.save(vocationEntity).id;
  }

  public Vocation get(final int id) {
    return gateway.find(VocationEntity.class).where().eq("id", id).unique().toVocation();
  }

  public Vocation update(final int id, final Vocation model) {
    final VocationEntity vocationEntity = VocationEntity.existedOne(id, model);
    return gateway.update(vocationEntity).toVocation();
  }

  public List<Tuple2<Integer, Vocation>> approvedFor(final int employeeId) {
    final List<VocationEntity> vocationEntities = gateway.find(VocationEntity.class)
        .where()
        .eq("employee_id", employeeId)
        .and()
        .eq("approved", 1)
        .list();
    return vocationEntities.stream()
        .map(entity -> new Tuple2<>(entity.id, entity.toVocation()))
        .collect(toList());
  }

  public List<Tuple2<Integer, Vocation>> pendingFor(final Integer employeeId) {
    final List<VocationEntity> awardEntities = gateway.find(VocationEntity.class).where()
        .eq("employee_id", employeeId)
        .and()
        .eq("approved", 0)
        .and()
        .eq("rejected", 0)
        .and()
        .eq("cancelled", 0)
        .list();
    return awardEntities.stream()
        .map(entity -> new Tuple2<>(entity.id, entity.toVocation()))
        .collect(toList());
  }

  @Entity
  @Table(name = "vocations")
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @EqualsAndHashCode
  @ToString
  private static class VocationEntity {
    @Id
    private final Integer id;

    private final UserEntity employee;

    private final Long timestamp;

    private final Long duration;

    private final Boolean approved;

    private final Boolean rejected;

    private final Boolean cancelled;

    static VocationEntity newOne(final Vocation vocation) {
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

    static VocationEntity existedOne(final int id, final Vocation vocation) {
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

    Vocation toVocation() {
      return new Vocation(employee, timestamp, duration, approved, rejected, cancelled);
    }
  }
}
