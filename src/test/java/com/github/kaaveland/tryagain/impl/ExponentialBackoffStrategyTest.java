package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.DelayStrategy;
import com.github.kaaveland.tryagain.impl.ExponentialBackoffStrategy;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ExponentialBackoffStrategyTest {

    private DelayStrategy base4 = new ExponentialBackoffStrategy(4);
    private DelayStrategy base2 = new ExponentialBackoffStrategy(2);

    @Test
    public void that_the_first_attempt_delays_baseBackoff() {
        assertThat(base2.delay(1), equalTo(2L));
        assertThat(base4.delay(1), equalTo(4L));
    }

    @Test
    public void that_the_second_attempt_delays_square_of_baseBackoff() {
        assertThat(base2.delay(2), equalTo(4L));
        assertThat(base4.delay(2), equalTo(16L));
    }

    @Test
    public void that_the_third_attempt_delays_cube_of_baseBackoff() {
        assertThat(base2.delay(3), equalTo(8L));
        assertThat(base4.delay(3), equalTo(64L));
    }

}
