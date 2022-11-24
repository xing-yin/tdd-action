package com.alan.tdd.args.v2;

import com.alan.tdd.args.exceptions.TooManyArgumentException;

import java.util.List;

public class BooleanOptionParser implements OptionParser<Boolean> {
    @Override
    public Boolean parse(List<String> arguments, Option option) {
        int index = arguments.indexOf("-" + option.value());
        if (index == -1) return false;

        List<String> values = SingleValueOptionParser.values(arguments, index);
        if (values.size() > 0) {
            throw new TooManyArgumentException(option.value());
        }
        return true;
    }
}
