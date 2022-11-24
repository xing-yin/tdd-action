package com.alan.tdd.args;

import com.alan.tdd.args.exceptions.InsufficientException;
import com.alan.tdd.args.exceptions.TooManyArgumentException;
import com.alan.tdd.args.v2.Option;
import com.alan.tdd.args.v2.SingleValueOptionParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class SingleValueOptionParserTest {

    // SingleValuedOptionParserTest：
    // sad path：
    // todo:     - int -p / -p 8080 8081
    // todo:     - String -d /   -d /usr/logs /usr/vars
    // default value：
    // todo: -int 0
    // todo: -string 0

    // sad path
    @Test
    public void should_not_accept_extra_arguments_for_single_value_option() {
        TooManyArgumentException exception = assertThrows(TooManyArgumentException.class, () -> {
            new SingleValueOptionParser<>(0, Integer::parseInt).parse(asList("-p", "8080", "8081"), option("p"));
        });
        assertEquals("p", exception.getOption());
    }

    // 参数化的测试
    // sad path
    @ParameterizedTest
    @ValueSource(strings = {"-p -l", "-p"})
    public void should_not_accept_insufficient_arguments_for_single_value_option(String arguments) {
        InsufficientException exception = assertThrows(InsufficientException.class, () -> {
            new SingleValueOptionParser<>(0, Integer::parseInt).parse(asList(arguments.split(" ")), option("p"));
        });
        assertEquals("p", exception.getOption());
    }

    // default value
    @Test
    public void should_set_default_value_to_0_for_int_option() {
        assertEquals(0, new SingleValueOptionParser<>(0, Integer::parseInt).parse(asList(), option("p")));
    }

    // default value: 更加泛型的处理方式
    @Test
    public void should_set_default_value_to_0_for_int_option_generic() {
        Function<String,Object> whatever = (it) -> null;
        Object defaultValue = new Object();
        assertEquals(defaultValue, new SingleValueOptionParser<>(defaultValue, whatever).parse(asList(), option("p")));
    }

    // happy path
    @Test
    public void should_parse_value_if_flag_present() {
        assertEquals(8080, new SingleValueOptionParser<>(0, Integer::parseInt).parse(asList("-p","8080"), option("p")));
    }

    // happy path: 更加泛型的处理方式
    @Test
    public void should_parse_value_if_flag_present_generic() {
        Object parsed = new Object();
        Function<String,Object> parse = (it) -> parsed;
        Object whatever = new Object();
        assertSame(parsed, new SingleValueOptionParser<>(whatever, parse).parse(asList("-p","8080"), option("p")));
    }

    // should_parse_value_if_flag_present_generic 从「状态验证」转换为「行为验证」
    @Test
    public void should_parse_value_if_flag_present_generic2() {
        Function parser = Mockito.mock(Function.class);

        new SingleValueOptionParser<>(Mockito.any(), parser).parse(asList("-p","8080"),option("p"));

        Mockito.verify(parser).apply("8080");
    }

    @Test
    public void should_not_accept_extra_arguments_for_string_single_value_option() {
        TooManyArgumentException exception = assertThrows(TooManyArgumentException.class, () -> {
            new SingleValueOptionParser<>("", String::valueOf).parse(asList("-d", "/usr/logs", "/usr/var"), option("d"));
        });
        assertEquals("d", exception.getOption());
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