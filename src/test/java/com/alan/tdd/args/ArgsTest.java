package com.alan.tdd.args;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    // 实现思路1，将命令行分割成数组：[-l],[-p,8080],[-d,/usr/logs]
    // 实现思路2，当成 Map：{-l:[],-p:[8080],-d:[/usr/logs]}

    // 功能分解与任务列表
    // 1.single Option:
    // todo:     - Bool -l
    // todo:     - Integer -p 8080
    // todo:     - String -d /usr/logs
    // 2.multi Option: -l -p 8080 -d /usr/logs
    // 3.sad path
    //      - Bool -l t  /  -l t f
    //      - Integer -p / -p 8080 8081
    //      - String -d /   -d /usr/logs /usr/vars
    // 4.default value
    //      - Bool false
    //      - Integer 0
    //      - String ""


    // 1.single Option:- Bool -l
    @Test
    public void should_set_boolean_option_to_true_if_flag_present() {
        BooleanOption option = Args.parse(BooleanOption.class, "-l");
        assertTrue(option.logging());
    }

    @Test
    // 写个相反的情况，使得上面的测试迅速失败
    public void should_set_boolean_option_to_false_if_flag_not_present() {
        BooleanOption option = Args.parse(BooleanOption.class, "");
        assertFalse(option.logging());
    }

    static record BooleanOption(@Option("l") boolean logging) {
    }

    // 1.single Option:- Integer -p 8080
    @Test
    public void should_parse_int_as_option_value() {
        IntOption option = Args.parse(IntOption.class, "-p", "8080");
        assertEquals(8080, option.port());
    }

    static record IntOption(@Option("p") int port) {
    }

    // 1.single Option:- String -d /usr/logs
    @Test
    public void should_parse_string_as_option_value() {
        StringOption option = Args.parse(StringOption.class, "-d", "/usr/logs");
        assertEquals("/usr/logs", option.direction());
    }

    static record StringOption(@Option("d") String direction) {
    }

    // 2.multi Option: -l -p 8080 -d /usr/logs
    @Test
    public void should_parse_multi_option() {
        MultiOptions options = Args.parse(MultiOptions.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.logging());
        assertEquals(8080, options.port());
        assertEquals("/usr/logs", options.directory());
    }

//    @Test
//    public void should_parse_multi_option() {
//        ListOptions options = Args.parse(ListOptions.class, "-g", "this", "is", "a", "list", "-d", "1", "2", "-3", "5");
//        assertArrayEquals(new String[]{"this", "is", "a", "list"}, options.group());
//        assertArrayEquals(new int[]{1, 2, -3, 5}, options.decimals());
//    }


    // 3.sad path
    //      - Bool -l t  /  -l t f
    //      - Integer -p / -p 8080 8081
    //      - String -d /   -d /usr/logs /usr/vars
    // 4.default value
    //      - Bool false
    //      - Integer 0
    //      - String ""


    static record MultiOptions(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }

    static record ListOptions(@Option("g") String[] group, @Option("d") int[] decimals) {
    }
}
