package evgenyt.springdemo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Wrapper that used in JdbcTemplate.query() to get person list from DB
 */

public class PersonRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Person(resultSet.getInt("person_id"),
                resultSet.getString("person_name"));
    }
}
