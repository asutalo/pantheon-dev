package com.eu.atit.pantheon.service.data;

import com.eu.atit.pantheon.service.Service;

import java.util.List;
import java.util.Map;

public interface DataService<T, Q> extends Service {
    void update(T toUpdate) throws Exception;

    void save(T toSave) throws Exception;

    void delete(T toDelete) throws Exception;

    Q filteredSelect();

    T get(Q filteredSelect) throws Exception;

    T get(Map<String, Object> filter) throws Exception;

    List<T> getAll() throws Exception;

    List<T> getAll(Map<String, Object> filter) throws Exception;

    List<T> getAll(Q filteredSelect) throws Exception;

    T instanceOfT(Map<String, Object> values);
}
