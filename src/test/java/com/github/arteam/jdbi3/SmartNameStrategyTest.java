package com.github.arteam.jdbi3;

import com.github.arteam.jdbi3.strategies.NameStrategies;
import com.github.arteam.jdbi3.strategies.SmartNameStrategy;
import com.github.arteam.jdbi3.strategies.StatementNameStrategy;
import org.jdbi.v3.core.ExtensionMethod;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class SmartNameStrategyTest extends AbstractStrategyTest {

    private StatementNameStrategy smartNameStrategy = new SmartNameStrategy();
    private InstrumentedTimingCollector collector;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        collector = new InstrumentedTimingCollector(registry, smartNameStrategy);
    }

    @Test
    public void updatesTimerForSqlObjects() throws Exception {
        when(ctx.getExtensionMethod()).thenReturn(
                new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForSqlObjects")));

        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);

        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name(getClass(), "updatesTimerForSqlObjects"));
        assertThat(getTimerMaxValue(name)).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForRawSql() throws Exception {

        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);

        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name("sql", "raw"));
        assertThat(getTimerMaxValue(name)).isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForNoRawSql() throws Exception {
        reset(ctx);

        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);

        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name("sql", "empty"));
        assertThat(getTimerMaxValue(name)).isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForContextClass() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_CLASS)).thenReturn(getClass().getName());
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForContextClass");

        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);

        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name(getClass(), "updatesTimerForContextClass"));
        assertThat(getTimerMaxValue(name)).isEqualTo(3000000000L);
    }

    @Test
    public void updatesTimerForTemplateFile() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_GROUP)).thenReturn("foo/bar.stg");
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForTemplateFile");

        collector.collect(TimeUnit.SECONDS.toNanos(4), ctx);

        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name("foo", "bar", "updatesTimerForTemplateFile"));
        assertThat(getTimerMaxValue(name)).isEqualTo(4000000000L);
    }

    @Test
    public void updatesTimerForContextGroupAndName() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_GROUP)).thenReturn("my-group");
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForContextGroupAndName");

        collector.collect(TimeUnit.SECONDS.toNanos(4), ctx);

        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name("my-group", "updatesTimerForContextGroupAndName"));
        assertThat(getTimerMaxValue(name)).isEqualTo(4000000000L);
    }

    @Test
    public void updatesTimerForContextGroupTypeAndName() throws Exception {
        when(ctx.getAttribute(NameStrategies.STATEMENT_GROUP)).thenReturn("my-group");
        when(ctx.getAttribute(NameStrategies.STATEMENT_TYPE)).thenReturn("my-type");
        when(ctx.getAttribute(NameStrategies.STATEMENT_NAME)).thenReturn("updatesTimerForContextGroupTypeAndName");

        collector.collect(TimeUnit.SECONDS.toNanos(5), ctx);

        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name("my-group", "my-type", "updatesTimerForContextGroupTypeAndName"));
        assertThat(getTimerMaxValue(name)).isEqualTo(5000000000L);
    }
}
