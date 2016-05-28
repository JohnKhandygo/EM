package com.kspt.khandygo.persistence;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.Lists;
import static com.kspt.khandygo.persistence.PersistenceUtils.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class Finder {
  private final SQLServer server;

  private <T> T tryFindUnique(final Class<? extends T> clazz, final String condition) {
    final ResultSet selected = server.select(queryForClassWithCondition(clazz, condition));
    try {
      Verify.verify(selected.next(), "There is no entries satisfying condition.");
      final T mapped = map(selected, clazz);
      Verify.verify(!selected.next(), "Expected unique instance,  but got more.");
      return mapped;
    } catch (SQLException e) {
      throw new RuntimeException(
          format(
              "Error while fetching unique data for class %s %s.",
              clazz.getSimpleName(), condition));
    }
  }

  private <T> String queryForClassWithCondition(final Class<T> clazz, final String condition) {
    return format("SELECT * FROM %s %s", tableNameForClass(clazz), condition);
  }

  private <T> T map(final ResultSet rs, final Class<T> clazz)
  throws SQLException {
    final Constructor<?> constructor = findTheOnlyOneConstructorOf(clazz);
    final Object[] args = extractArgsForConstructor(rs, constructor);
    try {
      constructor.setAccessible(true);
      return (T) constructor.newInstance(args);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(
          format(
              "Cannot create instance of type %s using constructor %s.",
              clazz, constructor));
    }
  }

  private Object[] extractArgsForConstructor(
      final ResultSet rs,
      final Constructor<?> constructor) {
    final Parameter[] parameters = constructor.getParameters();
    final Object[] args = new Object[parameters.length];
    for (int i = 0; i < parameters.length; ++i) {
      Preconditions.checkState(parameters[i].isNamePresent(),
          "Parameter name not present. Please use -parameters options for javac.");
      args[i] = extractArgForConstructorParameter(rs, parameters[i]);
    }
    return args;
  }

  private Object extractArgForConstructorParameter(final ResultSet rs, final Parameter parameter) {
    final Class<?> parameterType = parameter.getType();
    if (isEntity(parameterType)) {

      final String referenceKeyColumnName = referenceKeyColumnName(parameter.getName());
      final Object extracted = extractValueFor(rs, referenceKeyColumnName);
      if (extracted == null) return null;
      final Class<?> entityParameterType = entityClassFor(parameterType);
      return find(entityParameterType).where().eq("id", extracted).unique();
    } else {
      final Object extracted = extractValueFor(rs, parameter.getName());
      if (!parameter.getType().isAssignableFrom(extracted.getClass())) {
        if (extracted instanceof Number) {
          final int extractedAsInt = ((Number) extracted).intValue();
          if (parameter.getType().equals(Integer.class)) {
            return extractedAsInt;
          } else if (parameter.getType().equals(Boolean.class)) {
            return extractedAsInt == 1;
          } else {
            throw new RuntimeException(
                format(
                    "Cannot cast %s of type %s to parameter %s of type %s",
                    extracted,
                    extracted.getClass().getSimpleName(),
                    parameter.getName(),
                    parameter.getType().getSimpleName()));
          }
        }
        if (extracted instanceof byte[]) {
          if (parameter.getType().equals(String.class)) {
            return new String((byte[]) extracted);
          } else {
            throw new RuntimeException(
                format(
                    "Cannot cast %s of type %s to parameter %s of type %s",
                    extracted,
                    extracted.getClass().getSimpleName(),
                    parameter.getName(),
                    parameter.getType().getSimpleName()));
          }
        } else {
          throw new RuntimeException(
              format(
                  "Cannot cast %s of type %s to parameter %s of type %s",
                  extracted,
                  extracted.getClass().getSimpleName(),
                  parameter.getName(),
                  parameter.getType().getSimpleName()));
        }
      }
      return extracted;
    }
  }

  public <T> SelectAllOrSpecifyCondition<T> find(final Class<T> clazz) {
    final Class<? extends T> toFind;
    if (clazz.isAnnotationPresent(Entity.class)) {
      toFind = clazz;
    } else {
      try {
        toFind = (Class<? extends T>) Class.forName(format("%sEntity", clazz.getName()));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(
            format(
                "Cannot search for class %s: it is not an entity and has no entity subclass.",
                clazz.getSimpleName()));
      }
    }
    Preconditions.checkState(toFind.isAnnotationPresent(Table.class),
        "Class, you want to search not annotated with Table.");
    return new SelectAllOrSpecifyCondition<>(toFind);
  }

  private <T> List<T> tryFindAll(final Class<? extends T> clazz, final String condition) {
    final ResultSet selected = server.select(queryForClassWithCondition(clazz, condition));
    final List<T> mapped = Lists.newArrayList();
    try {
      while (selected.next()) mapped.add(map(selected, clazz));
    } catch (SQLException e) {
      throw new RuntimeException(
          format(
              "Error while fetching all data for class %s %s.",
              clazz.getSimpleName(), condition));
    }
    return mapped;
  }

  @AllArgsConstructor
  public class SelectAllOrSpecifyCondition<T> {
    private final Class<? extends T> clazz;

    public ConditionBuilder where() {
      return new ConditionBuilder("WHERE ");
    }

    public List<T> all() {
      return tryFindAll(clazz, "");
    }

    @AllArgsConstructor
    public class ConditionBuilder {

      private final String conditionString;

      public ConditionBuilder eq(final String parameter, final Object o) {
        final String continuationFormat;
        if (o instanceof String)
          continuationFormat = "%s %s=\"%s\"";
        else
          continuationFormat = "%s %s=%s";
        return new ConditionBuilder(format(continuationFormat, conditionString, parameter, o));
      }

      public ConditionBuilder moreThen(final String parameter, final Object o) {
        final String continuationFormat;
        if (o instanceof String)
          continuationFormat = "%s %s>\"%s\"";
        else
          continuationFormat = "%s %s>%s";
        return new ConditionBuilder(format(continuationFormat, conditionString, parameter, o));
      }

      public ConditionBuilder asc(final String parameter) {
        final String continuationFormat;
        if (conditionString.endsWith(" ASC") || conditionString.endsWith(" DESC")) {
          continuationFormat = "%s, %s ASC";
        } else {
          continuationFormat = "%s ORDER BY %s ASC";
        }
        return new ConditionBuilder(format(continuationFormat, conditionString, parameter));
      }

      public ConditionBuilder desc(final String parameter) {
        final String continuationFormat = "%s ORDER BY %s DESC";
        return new ConditionBuilder(format(continuationFormat, conditionString, parameter));
      }

      public <T> ConditionBuilder in(final String parameter, final List<T> os) {
        final List<Object> formattedElements = os.stream()
            .map(o -> o instanceof String ? format("'%s'", o) : o)
            .collect(toList());
        final String sequence = Joiner.on(",").join(formattedElements);
        final String continuationFormat = "%s %s in (%s)";
        return new ConditionBuilder(
            format(continuationFormat, conditionString, parameter, sequence));
      }

      public ConditionBuilder and() {
        return new ConditionBuilder(format("%s AND", conditionString));
      }

      public ConditionBuilder or() {
        return new ConditionBuilder(format("%s OR", conditionString));
      }

      public T unique() {
        return tryFindUnique(clazz, conditionString);
      }

      public List<T> list() {
        return tryFindAll(clazz, conditionString);
      }
    }
  }
}
