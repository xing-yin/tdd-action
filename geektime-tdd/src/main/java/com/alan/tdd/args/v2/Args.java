package com.alan.tdd.args.v2;

import com.alan.tdd.args.exceptions.IllegalOptionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Args {
    public static <T> T parse(Class<T> optionsClass, String... args) {
        try {
            List<String> arguments = Arrays.asList(args);
            Constructor<?> constructor = optionsClass.getDeclaredConstructors()[0];

            Object[] values = Arrays.stream(constructor.getParameters()).map(it -> parseOption(arguments, it)).toArray();
            return (T) constructor.newInstance(values);
        } catch (IllegalOptionException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object parseOption(List<String> arguments, Parameter parameter) {
        if (!parameter.isAnnotationPresent(Option.class)) {
            throw new IllegalOptionException(parameter.getName());
        }
        Option option = parameter.getAnnotation(Option.class);

        Class<?> type = parameter.getType();
        return getOptionParser(type).parse(arguments, option);
    }

//    // 重构前
//    private static OptionParser getOptionParser(Class<?> type) {
//        OptionParser parser = null;
//        if (type == boolean.class) {
//            parser = new BooleanParser();
//        }
//        if (type == int.class) {
//            parser = new IntParser();
//        }
//        if (type == String.class) {
//            parser = new StringParser();
//        }
//        return parser;
//    }

    // 重构后
    // 利用一个 Map 查表法优化 getOptionParser
    private static Map<Class<?>, OptionParser> PARSER = Map.of(
            boolean.class, new BooleanOptionParser(),
            int.class, new SingleValueOptionParser<>(0, Integer::parseInt),
            String.class, new SingleValueOptionParser<>("", String::valueOf));

    private static OptionParser getOptionParser(Class<?> type) {
        return PARSER.get(type);
    }

}
