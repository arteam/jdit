package com.github.arteam.jdbi3;

import com.github.arteam.jdbi3.strategies.NameStrategies;
import com.github.arteam.jdbi3.strategies.ShortNameStrategy;
import org.jdbi.v3.core.ExtensionMethod;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ShortNameStrategyTest extends AbstractStrategyTest {

    private ShortNameStrategy shortNameStrategy = new ShortNameStrategy("jdbi");
    private InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, shortNameStrategy);

    @Test
    public void updatesTimerForShortSqlObjectStrategy() throws Exception {
        when(ctx.getExtensionMethod()).thenReturn(
                new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForShortSqlObjectStrategy")));

        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);

        String name = shortNameStrategy.getStatementName(ctx);

        assertThat(name).isEqualTo(name("jdbi", getClass().getSimpleName(),
                "updatesTimerForShortSqlObjectStrategy"));
        assertThat(getTimerMaxValue(name)).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForShortContextClassStrategy() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_CLASS)).thenReturn(getClass().getName());
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForShortContextClassStrategy");

        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);

        String name = shortNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name("jdbi", getClass().getSimpleName(),
                "updatesTimerForShortContextClassStrategy"));
        assertThat(getTimerMaxValue(name)).isEqualTo(3000000000L);
    }
}
