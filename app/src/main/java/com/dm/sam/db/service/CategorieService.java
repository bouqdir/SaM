package com.dm.sam.db.service;

import android.content.Context;
import android.database.Cursor;
import com.dm.sam.db.dao.SQLiteCategorieDao;
import com.dm.sam.model.Categorie;
import java.util.List;

public class CategorieService implements ServiceDAO<Categorie> {

    private static CategorieService instance;
    private SQLiteCategorieDao sqLiteCategorieDao;

    private CategorieService(Context context){
        sqLiteCategorieDao = SQLiteCategorieDao.getInstance(context);
    }

    public static CategorieService getInstance(Context context) {
        if (instance == null)
            instance = new CategorieService(context);
        return instance;
    }

    @Override
    public long create(Categorie object) {
        return sqLiteCategorieDao.create(object);
    }

    @Override
    public int update(Categorie object) {
        return sqLiteCategorieDao.update(object);
    }

    @Override
    public int delete(long id) {
        return sqLiteCategorieDao.delete(id);
    }

    @Override
    public Categorie findById(long id) {
        return sqLiteCategorieDao.findById(id);
    }

    public Categorie findByName(String name) {
       return sqLiteCategorieDao.findByName(name);
    }

    @Override
    public List<Categorie> findAll() {
        return sqLiteCategorieDao.findAll();
    }

    @Override
    public Cursor getWithCursor(long id) {
        return sqLiteCategorieDao.getWithCursor(id);
    }

    public int getCategoriesCount() {
        return sqLiteCategorieDao.getCategoriesCount();
}

    public void setDefaultCategories(){
         sqLiteCategorieDao.setDefaultCategories();
    }
}
