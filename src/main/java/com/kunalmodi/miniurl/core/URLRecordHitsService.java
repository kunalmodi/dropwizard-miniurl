package com.kunalmodi.miniurl.core;

import com.kunalmodi.miniurl.db.URLRecordDAO;
import io.dropwizard.lifecycle.Managed;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service to async record metrics on how often URLs are used.
 *
 * Buffers metrics in memory, and flushes them to the db every 1 minute.
 */
public class URLRecordHitsService implements Managed {

    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    private final HashMap<String, Integer> pending;
    private final URLRecordDAO dao;

    public URLRecordHitsService(URLRecordDAO dao) {
        this.dao = dao;

        this.pending = new HashMap<>();
    }

    @Override
    public void start() {
        service.scheduleAtFixedRate(new Job(), 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void stop() {
        service.shutdown();
    }

    public void scheduleRecordHit(String slug) {
        synchronized (pending) {
            Integer count = pending.getOrDefault(slug, 0);
            pending.put(slug, count + 1);
        }
    }

    private class Job implements Runnable {

        @Override
        public void run() {
            HashMap<String, Integer> toWrite = new HashMap<>();
            synchronized (pending) {
                toWrite.putAll(pending);
                pending.clear();
            }

            for (Map.Entry<String, Integer> entry : toWrite.entrySet()) {
                System.out.format("[URLRecordHitsService] updating %s by %d\n", entry.getKey(), entry.getValue());
                try {
                    dao.update(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    System.out.format("[URLRecordHitsService] got error: %s\n", e.toString());
                }
            }
        }
    }
}
