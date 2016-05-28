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
import javax.persistence.Table;

@Entity
@Table(name = "users")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode
@ToString
class UserEntity implements Employee {
  @Id
  private final Integer id;

  private final String login;

  private final String name;

  private final UserEntity manager;

  private final UserEntity paymaster;
}
