package com.kspt.khandygo.em.core;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
@ToString
public class Award {

  private final Employee employee;

  private final long when;

  private final long amount;

  private final boolean approved;

  private final boolean rejected;

  private final boolean cancelled;

  public Award approve() {
    Preconditions.checkState(!approved);
    Preconditions.checkState(!rejected);
    Preconditions.checkState(!cancelled);
    return new Award(employee, when, amount, true, false, false);
  }

  public Award reject() {
    Preconditions.checkState(!approved);
    Preconditions.checkState(!rejected);
    Preconditions.checkState(!cancelled);
    return new Award(employee, when, amount, false, true, false);
  }

  public Award cancel() {
    Preconditions.checkState(!approved);
    Preconditions.checkState(!rejected);
    Preconditions.checkState(!cancelled);
    return new Award(employee, when, amount, false, false, true);
  }

  public static Award newOne(
      final Employee employee,
      final long when,
      final long amount) {
    return new Award(employee, when, amount, false, false, false);
  }
}
