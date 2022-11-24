package com.alan.tdd.args.v2;

import com.alan.tdd.args.exceptions.InsufficientException;
import com.alan.tdd.args.exceptions.TooManyArgumentException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class SingleValueOptionParser<T> implements OptionParser<T> {

    Function<String, T> valueParser;

    T defaultValue;

    public SingleValueOptionParser(T defaultValue, Function<String, T> valueParser) {
        this.valueParser = valueParser;
        this.defaultValue = defaultValue;
    }


    // 重构的方式2
    @Override
    public T parse(List<String> arguments, Option option) {
        int index = arguments.indexOf("-" + option.value());
        if (index == -1) {
            return defaultValue;
        }

        List<String> values = values(arguments, index);


        if (values.size() < 1) {
            throw new InsufficientException(option.value());
        }
        if (values.size() > 1) {
            throw new TooManyArgumentException(option.value());
        }

        String value = values.get(0);
        return valueParser.apply(value);
    }

    protected static List<String> values(List<String> arguments, int index) {
        int followingFlag = IntStream.range(index + 1, arguments.size())
                .filter(it -> arguments.get(it).startsWith("-"))
                .findFirst().orElse(arguments.size());
        return arguments.subList(index + 1, followingFlag);
    }


//    // 重构的方式1
//    @Override
//    public T parse(List<String> arguments, Option option) {
//        int index = arguments.indexOf("-" + option.value());
//        if (index == -1) {
//            return defaultValue;
//        }
//
//        if (isReachEndOfList(index + 1, arguments.size()) ||
//                isFollowedByOtherFlag(arguments, index)) {
//            throw new InsufficientException(option.value());
//        }
//
//        if (secondArgumentIsNotAFlag(arguments, index)) {
//            throw new TooManyArgumentException(option.value());
//        }
//        String value = arguments.get(index + 1);
//        return valueParser.apply(value);
//    }
//
//    private static boolean secondArgumentIsNotAFlag(List<String> arguments, int index) {
//        return index + 2 < arguments.size() &&
//                !arguments.get(index + 2).startsWith("-");
//    }
//
//    private static boolean isFollowedByOtherFlag(List<String> arguments, int index) {
//        return arguments.get(index + 1).startsWith("-");
//    }
//
//    private static boolean isReachEndOfList(int index, int arguments) {
//        return index == arguments;
//    }

}
