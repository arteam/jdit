package com.github.arteam.jdit;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import org.jdbi.v3.core.Jdbi;

import java.util.Properties;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_latest;

public class MySqlDBIFactory implements DBIFactory {

    private DBIFactory dbiFactory = new StandardDBIFactory();

    @Override
    public Jdbi createDBI(Properties properties) {
        MysqldConfig config = aMysqldConfig(v5_7_latest)
                .withPort(33306)
                .withUser("jdit", "test")
                .build();
        EmbeddedMysql mysql = anEmbeddedMysql(config)
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                mysql.stop();
            }
        }));
        return dbiFactory.createDBI(properties);
    }
}
