package com.github.arteam.jdbi3.strategies;

import com.codahale.metrics.MetricRegistry;
import org.jdbi.v3.core.statement.StatementContext;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractStrategyTest {

    protected MetricRegistry registry = new MetricRegistry();
    protected StatementContext ctx = mock(StatementContext.class);

    @Before
    public void setUp() throws Exception {
        when(ctx.getRawSql()).thenReturn("SELECT 1");
    }

    protected long getTimerMaxValue(String name) {
        return registry.timer(name).getSnapshot().getMax();
    }
}
