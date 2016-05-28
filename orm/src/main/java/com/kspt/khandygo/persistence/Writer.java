package com.kspt.khandygo.persistence;

import com.google.common.base.Preconditions;
import static com.kspt.khandygo.persistence.PersistenceUtils.*;
import static java.lang.String.format;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;

@AllArgsConstructor(onConstructor = @__({@Inject}))
class Writer {
  private final SQLServer server;

  <T> T save(final T toSave) {
    Class<?> clazz = toSave.getClass();
    final String tableName = clazz.getAnnotation(Table.class).name();
    Preconditions.checkState(isEntity(clazz),
        "Class %s not annotated with Entity. Cannot save.",
        clazz.getSimpleName());
    final Integer id = getKeyValue(toSave);
    Preconditions.checkState(id == null,
        "Entry %s already has id. Cannot save. Use update method instead.", toSave);
    final StringBuilder columnNamesBuilder = new StringBuilder();
    final StringBuilder columnValueBuilder = new StringBuilder();
    while (true) {
      for (final Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        if (isEntity(field.getType())) {
          columnNamesBuilder.append(referenceKeyColumnName(field.getName())).append(",");
          columnValueBuilder.append(extractKeyValueOrSaveIfNull(toSave, field)).append(",");
        } else {
          columnNamesBuilder.append(field.getName()).append(",");
          if (field.getType().equals(String.class)) {
            columnValueBuilder
                .append("'")
                .append(getFieldValueOf(toSave, field))
                .append("'")
                .append(",");
          } else if (field.getType().equals(Boolean.class)) {
            final Boolean fieldValue = (Boolean) getFieldValueOf(toSave, field);
            columnValueBuilder.append(fieldValue ? 1 : 0).append(",");
          } else {
            columnValueBuilder.append(getFieldValueOf(toSave, field)).append(",");
          }
        }
      }
      if (clazz.getSuperclass() == null) break;
      clazz = clazz.getSuperclass();
    }
    String insert = format("INSERT INTO %s(%s) VALUES(%s)",
        tableName,
        columnNamesBuilder.deleteCharAt(columnNamesBuilder.length() - 1).toString(),
        columnValueBuilder.deleteCharAt(columnValueBuilder.length() - 1).toString());
    final int key = server.insert(insert);
    setKeyValue(toSave, key);
    return toSave;
  }

  <T> T update(final T toUpdate) {
    final Class<?> clazz = toUpdate.getClass();
    Preconditions.checkState(isEntity(clazz),
        "Class %s not annotated with Entity. Cannot update.",
        clazz.getSimpleName());
    Integer id = getKeyValue(toUpdate);
    Preconditions.checkNotNull(id,
        "Entry %s already has no id. Cannot update. Use save method instead.", toUpdate);

    final StringBuilder updateStatements = new StringBuilder();
    for (final Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getType().isAnnotationPresent(Table.class)) {
        updateStatements.append(referenceKeyColumnName(field.getName())).append("=");
        final Object nestedEntry = getFieldValueOf(toUpdate, field);
        Integer nestedEntryKey = getKeyValue(nestedEntry);
        Preconditions.checkNotNull(nestedEntryKey);
        updateStatements.append(nestedEntryKey).append(",");
      } else if (!field.isAnnotationPresent(Id.class)) {
        updateStatements.append(field.getName()).append("=");
        if (field.getType().equals(String.class)) {
          updateStatements
              .append("'")
              .append(getFieldValueOf(toUpdate, field))
              .append("'")
              .append(",");
        } else if (field.getType().equals(Boolean.class)) {
          final Boolean fieldValue = (Boolean) getFieldValueOf(toUpdate, field);
          updateStatements.append(fieldValue ? 1 : 0).append(",");
        } else {
          updateStatements.append(getFieldValueOf(toUpdate, field)).append(",");
        }
      }
    }
    String update = format("UPDATE %s SET %s WHERE id=%s",
        clazz.getAnnotation(Table.class).name(),
        updateStatements.deleteCharAt(updateStatements.length() - 1).toString(),
        id);
    server.update(update);
    return toUpdate;
  }

  private <T> Object extractKeyValueOrSaveIfNull(final T toSave, final Field field) {
    final Object nestedEntry = getFieldValueOf(toSave, field);
    Integer nestedEntryKey = getKeyValue(nestedEntry);
    if (nestedEntryKey == null) {
      final Object nestedSaved = save(nestedEntry);
      nestedEntryKey = getKeyValue(nestedSaved);
    }
    Preconditions.checkNotNull(nestedEntryKey);
    return nestedEntryKey;
  }
}
