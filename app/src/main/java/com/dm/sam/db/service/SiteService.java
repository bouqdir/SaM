package com.dm.sam.db.service;

import android.content.Context;
import android.database.Cursor;
import com.dm.sam.db.dao.SQLiteSiteDao;
import com.dm.sam.model.Categorie;
import com.dm.sam.model.Site;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SiteService implements ServiceDAO<Site>{

    private static SiteService instance;
    private SQLiteSiteDao sqLiteSiteDao;

    private SiteService(Context context){
        sqLiteSiteDao = SQLiteSiteDao.getInstance(context);
    }

    public static SiteService getInstance(Context context) {
        if (instance == null)
            instance = new SiteService(context);
        return instance;
    }

    @Override
    public long create(Site object) {
        return sqLiteSiteDao.create(object);
    }

    @Override
    public int update(Site object) {
        return sqLiteSiteDao.update(object);
    }

    @Override
    public int delete(long id) {
        return sqLiteSiteDao.delete(id);
    }

    @Override
    public Site findById(long id) {
        return sqLiteSiteDao.findById(id);
    }

    @Override
    public List<Site> findAll() {
        return sqLiteSiteDao.findAll();
    }

    public List<Site> findByCategory(long idCat) {
        return sqLiteSiteDao.findByCategory(idCat);
    }

    public Site findByLatLng(LatLng latLng){
        return sqLiteSiteDao.findByLatLng(latLng);
    }

    @Override
    public Cursor getWithCursor(long id) {
        return sqLiteSiteDao.getWithCursor(id);
    }



    public boolean isCategoryUsed(Categorie category) {
        return sqLiteSiteDao.isCategoryUsed(category);
    }

    public void createDefaultSitesIfNeed()  {
        sqLiteSiteDao.createDefaultSitesIfNeed();
    }
    public int getSitesCount() {
        return sqLiteSiteDao.getSitesCount();
    }


}
