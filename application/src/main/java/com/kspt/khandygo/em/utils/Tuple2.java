package com.kspt.khandygo.em.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Tuple2<T1, T2> {
  public final T1 _1;

  public final T2 _2;
}
