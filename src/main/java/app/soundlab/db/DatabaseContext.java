package app.soundlab.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DatabaseContext {
    private DatabaseContext() {
    }

    public static int update(String sql, StatementBinder binder) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database update failed", e);
        }
    }

    public static <T> List<T> query(String sql, StatementBinder binder, RowMapper<T> mapper) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(mapper.map(resultSet));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    public static <T> Optional<T> queryForObject(String sql, StatementBinder binder, RowMapper<T> mapper) {
        List<T> results = query(sql, binder, mapper);
        return results.stream().filter(Objects::nonNull).findFirst();
    }

    @FunctionalInterface
    public interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet resultSet) throws SQLException;
    }
}

