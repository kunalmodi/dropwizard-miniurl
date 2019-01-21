package com.kunalmodi.miniurl.core;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.HibernateException;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.security.SecureRandom;

public class RandomSlugGenerator implements IdentifierGenerator {
    private static final int MAX_TRIES = 3;
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor,
                                 Object o) throws HibernateException {
        for (int i = 0; i < MAX_TRIES; i++) {
            String slug = random(8);
            Query query = sharedSessionContractImplementor.createQuery(
                    "SELECT 1 FROM URLRecord u WHERE u.slug = :slug");
            query.setParameter("slug", slug);
            if (query.uniqueResult() == null) return slug;
        }
        throw new HibernateException("unique slug impossible to find");
    }

    public String random(int len) {
        StringBuilder s = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            s.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return s.toString();
    }
}
