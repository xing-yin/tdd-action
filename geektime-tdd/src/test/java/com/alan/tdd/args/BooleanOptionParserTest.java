package com.alan.tdd.args;

import com.alan.tdd.args.exceptions.TooManyArgumentException;
//import com.alan.tdd.args.v2.BooleanOptionParser;
import com.alan.tdd.args.v2.BooleanOptionParser;
import com.alan.tdd.args.v2.Option;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class BooleanOptionParserTest {

    // 调整任务列表(对应上面的 3，4：选择粒度更小的测试，这样更有益于问题的定位)
    // BooleanOptionParserTest:
    // sad path：
    // todo:     - Bool -l t  /  -l t f
    // default value：
    // todo:       - Bool false
    @Test
    public void should_not_accept_extra_argument_for_boolean_option() {
        TooManyArgumentException exception = assertThrows(TooManyArgumentException.class, () ->
                new BooleanOptionParser().parse(asList("-l", "t"), option("l")));
        assertEquals("l", exception.getOption());
    }

    @Test//Sad path
    public void should_not_accept_extra_arguments_for_boolean_option() {
        TooManyArgumentException exception = assertThrows(TooManyArgumentException.class, () ->
                new BooleanOptionParser().parse(asList("-l", "t", "f"), option("l")));
        assertEquals("l", exception.getOption());
    }

    @Test//Sad path
    public void should_set_default_value_to_false_if_option_not_present() {
        assertFalse(new BooleanOptionParser().parse(asList(), option("l")));
    }


    // 测试代码重构：按照测试策略重组测试
    @Test // Happy path
    public void should_set_default_value_to_true_if_option_present() {
        assertTrue(new BooleanOptionParser().parse(asList("-l"), option("l")));
    }

    public Option option(String value) {
        return new Option() {
            @Override
            public String value() {
                return value;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Option.class;
            }
        };
    }
}
