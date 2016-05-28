package com.kspt.khandygo.persistence;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

class PersistenceUtils {

  static String tableNameForClass(final Class<?> clazz) {
    if (clazz.isAnnotationPresent(Table.class))
      return clazz.getAnnotation(Table.class).name();
    else
      return format("%ss", clazz.getSimpleName());
  }

  static Constructor<?> findTheOnlyOneConstructorOf(final Class<?> clazz) {
    final Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
    Verify.verify(declaredConstructors.length == 1,
        "Expecting class %s to has only one constructor, but got %d.",
        declaredConstructors.length);
    return declaredConstructors[0];
  }

  static String referenceKeyColumnName(final String referenceColumnName) {
    return format("%s_id", referenceColumnName);
  }

  static Object extractValueFor(final ResultSet rs, final String columnName) {
    try {
      return rs.getObject(columnName);
    } catch (SQLException e) {
      throw new RuntimeException(
          format(
              "Error while extracting value of column %s from result set.",
              columnName),
          e);
    }
  }

  static Object extractNonNullValueFor(final ResultSet rs, final String columnName) {
    try {
      return requireNonNull(
          rs.getObject(columnName),
          format(
              "Column %s has null value in result set.",
              columnName));
    } catch (SQLException e) {
      throw new RuntimeException(
          format(
              "Error while extracting value of column %s from result set.",
              columnName),
          e);
    }
  }

  static boolean isEntity(final Class<?> clazz) {
    return clazz.isAnnotationPresent(Entity.class) || classExists(dualEntityClassName(clazz));
  }

  private static String dualEntityClassName(final Class<?> clazz) {
    return format("%sEntity", clazz.getName());
  }

  private static boolean classExists(final String fullClassName) {
    try {
      Class.forName(fullClassName);
    } catch (ClassNotFoundException e) {
      return false;
    }
    return true;
  }

  static <T> Class<? extends T> entityClassFor(final Class<T> clazz) {
    if (clazz.isAnnotationPresent(Entity.class)) return clazz;
    try {
      return (Class<? extends T>) Class.forName(dualEntityClassName(clazz));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(
          format(
              "Cannot approvedFor dual entity class for class %s.",
              clazz.getName()));
    }
  }

  static Object getFieldValueOf(final Object object, final Field field) {
    try {
      return field.get(object);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(
          format(
              "Cannot extract value of field %s from %s.",
              field.getName(), object),
          e);
    }
  }

  static <T> Integer getKeyValue(final T entry) {
    final Field keyField = findKeyField(entry.getClass());
    keyField.setAccessible(true);
    try {
      return (Integer) keyField.get(entry);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(
          format(
              "Cannot extract key value for entry %s.",
              entry),
          e);
    }
  }

  private static Field findKeyField(final Class<?> clazz) {
    final Field[] declaredFields = clazz.getDeclaredFields();
    for (final Field declaredField : declaredFields) {
      if (isKeyField(declaredField)) {
        Preconditions.checkState(declaredField.getType().equals(Integer.class),
            "Id of entry expected to be of Integer type, but got %s.",
            declaredField.getType().getSimpleName());
        return declaredField;
      }
    }
    throw new RuntimeException(
        format(
            "Class %s has no fields, annotated with Id.",
            clazz.getClass().getSimpleName()));
  }

  private static boolean isKeyField(final Field declaredField) {
    return declaredField.isAnnotationPresent(Id.class);
  }

  static <T> T setKeyValue(final T entry, final int id) {
    final Field keyField = findKeyField(entry.getClass());
    keyField.setAccessible(true);
    try {
      keyField.set(entry, id);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(
          format(
              "Cannot set key value %s for entry %s on field %s.",
              id, entry, keyField.getName()),
          e);
    }
    return entry;
  }
}
