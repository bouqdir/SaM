package com.dm.sam.db.service;

import android.content.Context;
import android.database.Cursor;
import com.dm.sam.db.dao.SQLiteCategoryDao;
import com.dm.sam.model.Categorie;
import java.util.List;

public class CategorieService implements ServiceDAO<Categorie> {

    private static CategorieService instance;
    private SQLiteCategoryDao sqLiteCategoryDao;

    private CategorieService(Context context){
        sqLiteCategoryDao = SQLiteCategoryDao.getInstance(context);
    }

    public static CategorieService getInstance(Context context) {
        if (instance == null)
            instance = new CategorieService(context);
        return instance;
    }

    @Override
    public long create(Categorie object) {
        return sqLiteCategoryDao.create(object);
    }

    @Override
    public int update(Categorie object) {
        return sqLiteCategoryDao.update(object);
    }

    @Override
    public int delete(long id) {
        return sqLiteCategoryDao.delete(id);
    }

    @Override
    public Categorie findById(long id) {
        return sqLiteCategoryDao.findById(id);
    }

    public Categorie findByName(String name) {
       return sqLiteCategoryDao.findByName(name);
    }

    @Override
    public List<Categorie> findAll() {
        return sqLiteCategoryDao.findAll();
    }

    @Override
    public Cursor getWithCursor(long id) {
        return sqLiteCategoryDao.getWithCursor(id);
    }

    public int getCategoriesCount() {
        return sqLiteCategoryDao.getCategoriesCount();
}

    public void setDefaultCategories(){
         sqLiteCategoryDao.setDefaultCategories();
    }
}
