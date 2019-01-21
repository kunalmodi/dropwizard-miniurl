package com.kunalmodi.miniurl.db;

import com.kunalmodi.miniurl.core.URLRecord;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Date;

public class URLRecordDAO extends AbstractDAO<URLRecord> {
    private final SessionFactory factory;

    public URLRecordDAO(SessionFactory factory) {
        super(factory);
        this.factory = factory;
    }

    public URLRecord findBySlug(String slug) {
        return get(slug);
    }

    public void update(String slug, int total) throws HibernateException {
        // Since update is called outside of the scope of an existing session (@UnitofWork), we instead create a new
        // transaction to run this. I'd had to imagine there is a better way to do this.
        Session session = factory.openSession();
        session.beginTransaction();

        Query q = session.createQuery("UPDATE URLRecord SET hits = hits + :total, lastAccessed = :now WHERE slug = :slug");
        q.setParameter("slug", slug);
        q.setParameter("total", total);
        q.setParameter("now", new Date());
        q.executeUpdate();

        session.close();
    }

    public URLRecord create(String url) {
        URLRecord record = new URLRecord();
        record.setUrl(url);
        return persist(record);
    }
}
