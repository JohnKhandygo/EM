package com.kspt.khandygo.em.dao;

import com.kspt.khandygo.em.core.Employee;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = {"login"}))
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
@ToString
@SuppressWarnings("WeakerAccess")
public class UserEntity implements Employee {
  @Id
  private final Integer id;

  private final String login;

  private final String name;

  @ManyToOne
  private final UserEntity manager;

  @ManyToOne
  private final UserEntity paymaster;
}
