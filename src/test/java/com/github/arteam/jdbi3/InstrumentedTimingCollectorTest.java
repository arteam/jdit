package com.github.arteam.jdbi3;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.github.arteam.jdbi3.strategies.NameStrategies;
import com.github.arteam.jdbi3.strategies.ShortNameStrategy;
import com.github.arteam.jdbi3.strategies.SmartNameStrategy;
import com.github.arteam.jdbi3.strategies.StatementNameStrategy;
import org.jdbi.v3.core.ExtensionMethod;
import org.jdbi.v3.core.StatementContext;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class InstrumentedTimingCollectorTest {

    private final MetricRegistry registry = new MetricRegistry();
    private StatementNameStrategy smartNameStrategy = new SmartNameStrategy();
    private InstrumentedTimingCollector collector;
    private StatementContext ctx;

    @Before
    public void setUp() throws Exception {
        collector = new InstrumentedTimingCollector(registry, smartNameStrategy);
        ctx = mock(StatementContext.class);
        when(ctx.getRawSql()).thenReturn("SELECT 1");
    }

    @Test
    public void updatesTimerForSqlObjects() throws Exception {
        when(ctx.getExtensionMethod()).thenReturn(
                new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForSqlObjects")));

        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);

        final String name = smartNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name(getClass(), "updatesTimerForSqlObjects"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForRawSql() throws Exception {

        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);

        final String name = smartNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("sql", "raw"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForNoRawSql() throws Exception {
        reset(ctx);

        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);

        final String name = smartNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("sql", "empty"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForContextClass() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_CLASS)).thenReturn(getClass().getName());
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForContextClass");

        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);

        final String name = smartNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name(getClass(), "updatesTimerForContextClass"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(3000000000L);
    }

    @Test
    public void updatesTimerForTemplateFile() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_GROUP)).thenReturn("foo/bar.stg");
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForTemplateFile");

        collector.collect(TimeUnit.SECONDS.toNanos(4), ctx);

        final String name = smartNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("foo", "bar", "updatesTimerForTemplateFile"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(4000000000L);
    }

    @Test
    public void updatesTimerForContextGroupAndName() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_GROUP)).thenReturn("my-group");
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForContextGroupAndName");

        collector.collect(TimeUnit.SECONDS.toNanos(4), ctx);

        final String name = smartNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("my-group", "updatesTimerForContextGroupAndName"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(4000000000L);
    }

    @Test
    public void updatesTimerForContextGroupTypeAndName() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_GROUP)).thenReturn("my-group");
        when(ctx.getAttribute(NameStrategies.STATEMENT_TYPE)).thenReturn("my-type");
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForContextGroupTypeAndName");

        collector.collect(TimeUnit.SECONDS.toNanos(5), ctx);

        final String name = smartNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("my-group", "my-type", "updatesTimerForContextGroupTypeAndName"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(5000000000L);
    }

    @Test
    public void updatesTimerForShortSqlObjectStrategy() throws Exception {
        final StatementNameStrategy shortNameStrategy = new ShortNameStrategy("jdbi");
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, shortNameStrategy);
        when(ctx.getExtensionMethod()).thenReturn(
                new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForShortSqlObjectStrategy")));

        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);

        final String name = shortNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("jdbi", getClass().getSimpleName(),
                "updatesTimerForShortSqlObjectStrategy"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForShortContextClassStrategy() throws Exception {
        final StatementNameStrategy shortNameStrategy = new ShortNameStrategy("jdbi");
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, shortNameStrategy);
        when(ctx.getAttribute(NameStrategies.STATEMENT_CLASS)).thenReturn(getClass().getName());
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForShortContextClassStrategy");

        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);

        final String name = shortNameStrategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name).isEqualTo(name("jdbi", getClass().getSimpleName(),
                "updatesTimerForShortContextClassStrategy"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(3000000000L);
    }
}
