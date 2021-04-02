package com.barclays.ims;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/*
 * Interface defining abstract CRUD methods for each of your Data Access Objects.
 * Implementation of this will involve JDBC.
 */
public interface DAO<T> {

    List<T> readAll();

    T readById(Long id);

    T readLatest();

    T create(T t);

    T update(T t);

    int delete(Long id);

    T modelFromResultSet(ResultSet resultSet) throws SQLException;
}
