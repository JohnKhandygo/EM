package com.kspt.khandygo.persistence;

import com.google.common.base.Preconditions;
import static java.lang.String.format;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface SQLServer {
  ResultSet select(final String query);

  int insert(final String query);

  void update(final String query);

  static SQLServer newMySQLServer(
      final String host,
      final String port,
      final String scheme,
      final String user,
      final String password) {
    final String urlFormat
        = "jdbc:mysql://%s:%s/%s?user=%s&password=%s&trustServerCertificate=true&useSSL=true&requireSSL=true";
    final String url = format(urlFormat, host, port, scheme, user, password);
    try {
      final Connection connection = DriverManager.getConnection(url);
      return new MysqlServer(connection);
    } catch (SQLException e) {
      throw new RuntimeException("Cannot login to mysql database via provided parameters.");
    }
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  class MysqlServer implements SQLServer {
    private final Connection connection;

    @Override
    public ResultSet select(final String query) {
      final Statement statement = createStatement();
      final ResultSet resultSet;
      try {
        resultSet = statement.executeQuery(query);
      } catch (SQLException e) {
        throw new RuntimeException("Cannot execute query.", e);
      }
      closeStatement(statement);
      return resultSet;
    }

    private void closeStatement(final Statement statement) {
      try {
        statement.close();
      } catch (SQLException e) {
        throw new RuntimeException("Cannot close createStatement.");
      }
    }

    private Statement createStatement() {
      final Statement statement;
      try {
        statement = connection.createStatement();
      } catch (SQLException e) {
        throw new RuntimeException("Cannot create createStatement.", e);
      }
      return statement;
    }

    @Override
    public int insert(final String query) {
      final Statement statement;
      try {
        statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      } catch (SQLException e) {
        throw new RuntimeException("Cannot create createStatement.", e);
      }
      try {
        statement.executeUpdate(query);
      } catch (SQLException e) {
        throw new RuntimeException("Cannot execute query.", e);
      }
      final int generatedKey;
      try {
        final ResultSet generatedKeys = statement.getGeneratedKeys();
        Preconditions.checkState(generatedKeys.next());
        generatedKey = ((Long) generatedKeys.getObject(1)).intValue();
      } catch (SQLException e) {
        throw new RuntimeException("Cannot fetch generated keys.", e);
      }
      return generatedKey;
    }

    @Override
    public void update(final String query) {
      final Statement statement = createStatement();
      try {
        statement.executeUpdate(query);
      } catch (SQLException e) {
        throw new RuntimeException("Cannot execute query.", e);
      }
    }
  }
}