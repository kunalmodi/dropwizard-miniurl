package com.kunalmodi.miniurl.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.kunalmodi.miniurl.core.URLRecord;
import com.kunalmodi.miniurl.core.URLRecordHitsService;
import com.kunalmodi.miniurl.db.URLRecordDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class URLRecordResource {
    private URLRecordDAO dao;
    private URLRecordHitsService recordHitsService;

    private LoadingCache<String, URI> recordCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(
                    new CacheLoader<String, URI>() {
                        @Override
                        public URI load(String slug) {
                            URLRecord record = dao.findBySlug(slug);
                            if (record == null) return null;
                            try {
                                return new URI(record.getUrl());
                            } catch (URISyntaxException e) {
                                return null;
                            }
                        }
                    });

    public URLRecordResource(URLRecordDAO dao, URLRecordHitsService recordHitsService) {
        this.dao = dao;
        this.recordHitsService = recordHitsService;
    }

    @GET
    @Path("/{slug}")
    @UnitOfWork
    @Timed
    public Response get(@PathParam("slug") String slug) throws ExecutionException {
        URI maybeUri = recordCache.get(slug);
        if (maybeUri == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        recordHitsService.scheduleRecordHit(slug);
        return Response.temporaryRedirect(maybeUri).build();
    }

    @GET
    @Path("/{slug}/metrics")
    @UnitOfWork
    @Timed
    public Response getMetrics(@PathParam("slug") String slug) {
        URLRecord record = dao.findBySlug(slug);
        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(record).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @UnitOfWork
    @Timed
    public URLRecord create(@FormParam("url") String url) {
        return dao.create(url);
    }
}
