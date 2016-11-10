package com.kspt.khandygo.em.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class Tuple2<T1, T2> {
  public final T1 _1;

  public final T2 _2;

  @NonNull
  public static <T1, T2> Tuple2<T1, T2> of(final @NonNull T1 _1, final @NonNull T2 _2) {
    return new Tuple2<>(_1, _2);
  }
}
