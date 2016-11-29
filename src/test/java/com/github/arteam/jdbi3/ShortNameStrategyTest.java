package com.github.arteam.jdbi3;


import com.codahale.metrics.Timer;
import com.github.arteam.jdbi3.strategies.NameStrategies;
import com.github.arteam.jdbi3.strategies.ShortNameStrategy;
import com.github.arteam.jdbi3.strategies.StatementNameStrategy;
import org.jdbi.v3.core.ExtensionMethod;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ShortNameStrategyTest extends AbstractStrategyTest {

    private StatementNameStrategy shortNameStrategy;

    @Test
    public void updatesTimerForShortSqlObjectStrategy() throws Exception {
        shortNameStrategy = new ShortNameStrategy("jdbi");
        when(ctx.getExtensionMethod()).thenReturn(
                new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForShortSqlObjectStrategy")));

        new InstrumentedTimingCollector(registry, shortNameStrategy).collect(TimeUnit.SECONDS.toNanos(1), ctx);

        String name = shortNameStrategy.getStatementName(ctx);
        Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("jdbi", getClass().getSimpleName(),
                "updatesTimerForShortSqlObjectStrategy"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForShortContextClassStrategy() throws Exception {
        shortNameStrategy = new ShortNameStrategy("jdbi");
        when(ctx.getAttribute(NameStrategies.STATEMENT_CLASS)).thenReturn(getClass().getName());
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForShortContextClassStrategy");

        new InstrumentedTimingCollector(registry, shortNameStrategy).collect(TimeUnit.SECONDS.toNanos(3), ctx);

        String name = shortNameStrategy.getStatementName(ctx);
        Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("jdbi", getClass().getSimpleName(),
                "updatesTimerForShortContextClassStrategy"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(3000000000L);
    }
}
