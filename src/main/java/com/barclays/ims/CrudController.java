package com.barclays.ims;

import java.util.List;

/*
 * Interface defining abstract CRUD methods for each of your Controllers.
 */
public interface CrudController<T> {

    List<T> readAll();

    T readyById();

    T create();

    T update();

    int delete();

}
