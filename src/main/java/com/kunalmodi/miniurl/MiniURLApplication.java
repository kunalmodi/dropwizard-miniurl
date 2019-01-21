package com.kunalmodi.miniurl;

import com.kunalmodi.miniurl.core.URLRecord;
import com.kunalmodi.miniurl.core.URLRecordHitsService;
import com.kunalmodi.miniurl.db.URLRecordDAO;
import com.kunalmodi.miniurl.resources.URLRecordResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MiniURLApplication extends Application<MiniURLConfiguration> {

    private final HibernateBundle<MiniURLConfiguration> hibernate =
            new HibernateBundle<MiniURLConfiguration>(URLRecord.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(MiniURLConfiguration config) {
            return config.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new MiniURLApplication().run(args);
    }

    @Override
    public String getName() {
        return "MiniURL";
    }

    @Override
    public void initialize(final Bootstrap<MiniURLConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(final MiniURLConfiguration config,
                    final Environment environment) {
        final URLRecordDAO dao = new URLRecordDAO(hibernate.getSessionFactory());
        final URLRecordHitsService recordHitsService = new URLRecordHitsService(dao);

        environment.lifecycle().manage(recordHitsService);
        environment.jersey().register(new URLRecordResource(dao, recordHitsService));
    }

}
