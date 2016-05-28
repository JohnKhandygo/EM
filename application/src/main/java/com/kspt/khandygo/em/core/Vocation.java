package com.kspt.khandygo.em.core;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
@ToString
public class Vocation {
  private final Employee employee;

  private final long when;

  private final long duration;

  private final boolean approved;

  private final boolean rejected;

  private final boolean cancelled;

  public Vocation approve() {
    Preconditions.checkState(!approved);
    Preconditions.checkState(!rejected);
    Preconditions.checkState(!cancelled);
    return new Vocation(employee, when, duration, true, false, false);
  }

  public Vocation reject() {
    Preconditions.checkState(!approved);
    Preconditions.checkState(!rejected);
    Preconditions.checkState(!cancelled);
    return new Vocation(employee, when, duration, false, true, false);
  }

  public Vocation cancel() {
    Preconditions.checkState(!approved);
    Preconditions.checkState(!rejected);
    Preconditions.checkState(!cancelled);
    return new Vocation(employee, when, duration, false, false, true);
  }

  public static Vocation newOne(
      final Employee employee,
      final long when,
      final long duration) {
    final long sevenDays = TimeUnit.DAYS.toMillis(7);
    Preconditions.checkState(when > Instant.now().toEpochMilli() + sevenDays);
    Preconditions.checkState(duration >= sevenDays);
    final boolean approved = employee.manager() == null;
    return new Vocation(employee, when, duration, approved, false, false);
  }
}
