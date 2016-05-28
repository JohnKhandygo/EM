package com.kspt.khandygo.persistence;

import com.kspt.khandygo.persistence.Finder.SelectAllOrSpecifyCondition;
import lombok.AllArgsConstructor;
import javax.inject.Inject;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class Gateway {

  private final Finder finder;

  private final Writer writer;

  public <T> SelectAllOrSpecifyCondition<T> find(final Class<T> toFind) {

    return finder.find(toFind);
  }

  public <T> T save(final T toSave) {
    return writer.save(toSave);
  }

  public <T> T update(final T toUpdate) {
    return writer.update(toUpdate);
  }
}