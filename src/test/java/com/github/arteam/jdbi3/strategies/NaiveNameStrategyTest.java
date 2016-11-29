package com.github.arteam.jdbi3.strategies;

import com.github.arteam.jdbi3.InstrumentedTimingCollector;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class NaiveNameStrategyTest extends AbstractStrategyTest {

    private NaiveNameStrategy naiveNameStrategy = new NaiveNameStrategy();
    private InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, naiveNameStrategy);

    @Test
    public void producesSqlRawMetrics() throws Exception {
        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);

        String name = naiveNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualToIgnoringCase("sql.raw");
    }

}
