package com.kunalmodi.miniurl.core;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="url_records")
public class URLRecord {
    @Id
    @GenericGenerator(name="random_slug", strategy="com.kunalmodi.miniurl.core.RandomSlugGenerator")
    @GeneratedValue(generator="random_slug")
    @Column(name="slug", nullable=false, length=8)
    private String slug;

    @Column(name="url", nullable=false)
    @URL
    @NotEmpty
    private String url;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private Date createdAt;

    @Column(name="last_accessed")
    private Date lastAccessed;

    @Column(name="hits", nullable=false)
    private int hits = 0;

    public URLRecord() {}

    public URLRecord(String slug, String url, Date createdAt, Date lastAccessed, int hits) {
        this.slug = slug;
        this.url = url;
        this.createdAt = createdAt;
        this.lastAccessed = lastAccessed;
        this.hits = hits;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URLRecord urlRecord = (URLRecord) o;
        return hits == urlRecord.hits &&
                slug.equals(urlRecord.slug) &&
                url.equals(urlRecord.url) &&
                createdAt.equals(urlRecord.createdAt) &&
                Objects.equals(lastAccessed, urlRecord.lastAccessed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, url, createdAt, lastAccessed, hits);
    }
}
