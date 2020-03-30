package me.kirinrin.zuul.dao;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Classname PermissionDTOTest
 * @Description TODO
 * @Date 2020/3/13 3:58 下午
 * @author by Kirinrin
 */
class PermissionDTOTest {

    @Test
    void revertPermission() throws ClassNotFoundException {
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

        Map<String, Set<String>> result = PermissionDTO.revertPermission (data);
        for (String s : result.keySet()) {
            System.out.println("key = {}" + s);
            Set<String> idSets = result.get(s);
            for (String idSet : idSets) {
                System.out.println("\t idSet = " + idSet);
            }
        }

        assertEquals(result.size(),4);
        for (String s : result.keySet()) {
            assertEquals(result.get(s).size(),3);
        }
    }

    @Test
    void testURIstart(){
        String x = "/cloud-management/tenant";
        String y = "/cloud-managemnet";
        System.out.println(x.startsWith(y));
        System.out.println(y.startsWith(x));
        System.out.println(x.startsWith("/cloud-manage"));
    }
}