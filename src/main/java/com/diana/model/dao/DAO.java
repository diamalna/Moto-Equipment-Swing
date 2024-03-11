package com.diana.model.dao;

import com.diana.model.entities.Entity;

import java.sql.SQLException;
import java.util.List;

public interface DAO <K, V extends Entity>{
    public List<V> findAll();

    public V findByKey(K key);

    public boolean save(V obj) throws SQLException;

    public V update(K key, V newObj);

    public boolean delete(K key);

}
