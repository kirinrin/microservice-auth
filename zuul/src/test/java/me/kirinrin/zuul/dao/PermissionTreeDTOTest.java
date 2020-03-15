package me.kirinrin.zuul.dao;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Classname PermissionTreeDTOTest
 * @Description TODO
 * @Date 2020/3/15 6:05 下午
 * @Created by Kirinrin
 */
class PermissionMatchDTOTest {


    private List<PermissionDTO> getSomeTestData() {
        List<PermissionDTO> data = new ArrayList<>();
        data.add(PermissionDTO.builder().id("cas:analysis:DescribeMediaPlayer").uri("ref@cas:media-player:*").build());

        data.add(PermissionDTO.builder().id("cas:media-player:DescribeAutoTableInstanceAttribute").uri("ref@cas:auto-table:DescribeInstanceAttribute").build());
        data.add(PermissionDTO.builder().id("cas:media-player:DescribeDatasetItemInstanceAsr").uri("ref@cas:dataset:DescribeItemInstanceAsr").build());
        data.add(PermissionDTO.builder().id("cas:media-player:DescribeDatasetItemInstanceRecording").uri("ref@cas:dataset:DescribeItemInstanceRecording").build());
        data.add(PermissionDTO.builder().id("cas:media-player:DescribeQcTaskInstanceResult").uri("ref@cas:qc-task:DescribeInstanceResult").build());

        data.add(PermissionDTO.builder().id("cas:auto-table:DescribeInstanceAttribute").uri("get@qc/auto-table/{id}").build());
        data.add(PermissionDTO.builder().id("cas:dataset:DescribeItemInstanceAsr").uri("get@qc/data-set/item/{id}/asr").build());
        data.add(PermissionDTO.builder().id("cas:dataset:DescribeItemInstanceRecording").uri("get@qc/data-set/item/{id}/view-file").build());
        data.add(PermissionDTO.builder().id("cas:qc-task:DescribeInstanceResult").uri("get@qc/qc-task/{id}/result").build());
        return data;
    }

    @Test
    void testBuildUriMatchList(){
        List<PermissionMatchDTO> list = PermissionMatchDTO.buildUriMatchList(getSomeTestData());
        assertEquals(4, list.size());
    }

    @Test
    void testFindUri(){
        List<PermissionMatchDTO> list = PermissionMatchDTO.buildUriMatchList(getSomeTestData());
        PermissionMatchDTO match = PermissionMatchDTO.findMatchUri(list, "get@qc/data-set/item/134148334334442499x/view-file");
        assertNotNull(match);
        assertEquals(match.getUriMatchRe(), "get@qc/data-set/item/[A-Za-z0-9_]+/view-file");
        assertEquals(match.getPermission().getId(), "cas:dataset:DescribeItemInstanceRecording");
    }

    /**
     *  确定分割的方案
     */
    @Test
    void testSplitSulection(){
        String targent = "/assdee/";
        int a = targent.indexOf("/");
        System.out.println(targent + " a = " + a);
        System.out.println(targent.split("/").length);
        System.out.println(targent.split("/")[0] + "  " + targent.split("/")[1]);

        targent = "assdee/xxxx";
        a  = targent.indexOf("/");
        System.out.println(targent + " a = " + a);
        System.out.println(targent.split("/").length);

        targent = "xxx/";
        a  = targent.indexOf("/");
        System.out.println(targent + " a = " + a);
        System.out.println(targent.split("/").length);

        targent = "/";
        a  = targent.indexOf("/");
        System.out.println(targent + " a = " + a);
        System.out.println(targent.split("/").length);

        String uri = "/sdfasdfa/bbbb/xxxxx/";
        List x = Arrays.stream(uri.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        Queue<String> queue = new LinkedList<>(x);
        for (String s : queue) {
            System.out.println("queue = "+ s);
        }


    }

    /**
     * get@qc/data-set/item/134-148-3-34334442499x/view-file
     * 这样的查找到树中的 get@qc/data-set/item/{id}/view-file
     */
    @Test
    void matchReTest() {

        String re = "get@qc/data-set/item/[A-Za-z0-9]+/view-file";

        boolean x = Pattern.matches(re, "get@qc/data-set/item/134148334334442499x/view-file");
        assertTrue(x);

        Pattern p= Pattern.compile(re);

        Matcher matcher = p.matcher("get@qc/data-set/item/134148334334442499x/view-file");
        assertTrue(matcher.matches());

        matcher = p.matcher("get@qc/data-set/item/134148334334442499x/view-file/lookingfor");
        assertFalse(matcher.matches());

        matcher = p.matcher("get@qc/data-set/item/134148334334442499x");
        assertFalse(matcher.matches());

        matcher = p.matcher("get@qc/data-set/item/{id}/asr");
        assertFalse(matcher.matches());

        matcher = p.matcher("get@qc/data-set/item/{id}");
        assertFalse(matcher.matches());
    }
}