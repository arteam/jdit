package com.github.arteam.jdbi3.strategies;

import com.github.arteam.jdbi3.InstrumentedTimingCollector;
import org.jdbi.v3.core.ExtensionMethod;
import org.junit.Test;

import static com.codahale.metrics.MetricRegistry.name;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class BasicSqlNameStrategyTest extends AbstractStrategyTest {

    private BasicSqlNameStrategy basicSqlNameStrategy = new BasicSqlNameStrategy();
    private InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, basicSqlNameStrategy);

    @Test
    public void producesMethodNameAsMetric() throws Exception {
        when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForBasicSqlName")));
        String name = basicSqlNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(name(getClass(), "updatesTimerForBasicSqlName"));
    }

}
