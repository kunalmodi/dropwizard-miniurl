package com.kunalmodi.miniurl;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class MiniURLConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}
