package evgenyt.springdemo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Using Spring JDBC templates for SQL queries and parametrised queries
 * Aug 2019 EvgenyT
 */

public class App 
{
    public static void main( String[] args )
    {
        // Read context
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "applicationContext.xml");
        // Get dataSource bean
        DriverManagerDataSource dataSource = context.getBean("dataSource",
                DriverManagerDataSource.class);
        // DML command
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT INTO person(person_name) VALUES('Robin Hood')");
        // Get value from query
        int rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM person", Integer.class);
        System.out.println("Number of records: " + rowCount);
        // Get java objects from query using RowMapper
        final List<Person> personList = jdbcTemplate.query("SELECT * FROM person",
                new PersonRowMapper());
        System.out.println("All persons in table:");
        for (Person person : personList)
            System.out.println(person);
        // Butch update
        NamedParameterJdbcTemplate parameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(personList);
        int[] updatesCounts = parameterJdbcTemplate.batchUpdate(
                "UPDATE person SET person_name = 'Anonimous'", batch);
        System.out.println("Anonimised:" + updatesCounts[0]);
        // Batch insert with parameters
        System.out.println("Insterted:" + batchUpdateUsingJdbcTemplate(personList, jdbcTemplate)[0]);
        // Query with parameters
        SqlParameterSource sqlParameters = new MapSqlParameterSource()
                .addValue("person_id", 1);
        String personName = parameterJdbcTemplate.queryForObject(
                "SELECT person_name FROM person WHERE person_id = :person_id", sqlParameters, String.class);
        System.out.println("Name of person number 1 is " + personName);
    }

    // Parametrised by java objects DML
    public static int[] batchUpdateUsingJdbcTemplate(final List<Person> employees, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.batchUpdate("INSERT INTO person(person_name) VALUES (?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, employees.get(i).getName());
                    }
                    @Override
                    public int getBatchSize() {
                        return 50;
                    }
                });
    }
}
