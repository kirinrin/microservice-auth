package me.kirinrin.zuul.dao;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @Classname Policy业务数据
 * @Description 封装Policy数据
 * @Date 2020/3/13 11:37 上午
 * @Created by Kirinrin
 */
@Data
@Builder
@Slf4j
public class PermissionDTO {
    private String id;
    private String uri;

    /**
     * 翻转Permission数据对应关系
     * <p>
     * cas:analysis:DescribeMediaPlayer	                        ref@cas:media-player:*
     * <p>
     * cas:media-player:DescribeAutoTableInstanceAttribute	    ref@cas:auto-table:DescribeInstanceAttribute
     * cas:media-player:DescribeDatasetItemInstanceAsr	        ref@cas:dataset:DescribeItemInstanceAsr
     * cas:media-player:DescribeDatasetItemInstanceRecording	ref@cas:dataset:DescribeItemInstanceRecording
     * cas:media-player:DescribeQcTaskInstanceResult	        ref@cas:qc-task:DescribeInstanceResult
     * <p>
     * cas:auto-table:DescribeInstanceAttribute 	            get@qc/auto-table/{id}
     * cas:dataset:DescribeItemInstanceAsr	                    get@qc/data-set/item/{id}/asr
     * cas:dataset:DescribeItemInstanceRecording	            get@qc/data-set/item/{id}/view-file
     * cas:qc-task:DescribeInstanceResult	                    get@qc/qc-task/{id}/result
     * <p>
     * 下面的一对多的关系，翻转成如下，注意有 * 号通配的处理.
     * <p>
     * get@qc/auto-table/{id}   [cas:auto-table:DescribeInstanceAttribute,
     * cas:media-player:DescribeAutoTableInstanceAttribute,
     * cas:analysis:DescribeMediaPlayer]
     * 这里产生了问题要独立的去维护相关的对应关系，而且在新加了功能后需要维护这个表同时刷新缓存
     * 问题：
     * 1. 新加功能后，需要维护这个表，更新缓存后，用户再次登录才能在通过网关验证。例，在qc，下面新增了一个接口，
     * 这个接口不是一个单独的功能，只是页面某元素的功能提升或改进，这样的小改动，也需要来调整权限数据，刷新缓存，对于这个依赖过大。
     * 2. 关于{id}的模式判断过于复杂 get@qc/qc-task/{id}/result, 需要查表后配合通配符语法才能判断出来，比较不直观
     * 3. 基于现在的方案要求，功能维护与UI保持一致，丢失和 * 语法的录活性
     * 4. 如果出现更复杂以及更深层的ref -> ref - ref 或是 * ->ref - * 这样的数据完全不可读
     * <p>
     * 解决建议： 1. 参考RBAC的相关文档，可以尝试引入权限组对小权限进行封装。
     * 2. 慢慢修正，或从今以后，以新的URL规范来开发
     * <p>
     * 思路：为了便于解决所有复杂情况（虽然约定不会出现 ref-ref-ref x n）还是用递归吧
     *
     * @return
     */
    public static Map<String, Set<String>> revertPermission(List<PermissionDTO> srcData) throws ClassNotFoundException {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>(200);
        for (PermissionDTO next : srcData) {
            if (!next.getUri().startsWith("ref@")) {
                Set<String> idSets = new HashSet<String>();
                idSets.add(next.getId());
                map.put(next.getUri(), idSets);
            }
        }

        // 这种算法，只考虑了最外层有只有一层有* 的情况，对于 ref - ref* - uri 这种 或是多层 * 没有处理
        for (PermissionDTO next : srcData) {
            if (next.getUri().startsWith("ref@")) {
                String refUri = next.getUri().substring(4);
                for (PermissionDTO sub : srcData) {
                    if (match(refUri, sub.getId())) {
                        String finalUri = findMatchId(srcData, sub);
                        map.get(finalUri).add(next.getId());
                    }
                }
            }
        }


        return map;
    }


    private static String findMatchId(List<PermissionDTO> srcData, PermissionDTO next) throws ClassNotFoundException {
        Optional<PermissionDTO> optionPermission = null;
        if(next.getUri().startsWith("ref@")){
            String refUri = next.getUri().substring(4);
            optionPermission = srcData.stream().filter(d -> d.getId().equals(refUri)).findFirst();
        }else{
            return next.getUri();
        }
        if (optionPermission.isPresent()) {
            PermissionDTO orgPermission = optionPermission.get();
            log.debug("查找到最终 {} -> {}", next.getUri(), orgPermission);
            if (orgPermission.getUri().startsWith("ref@")) {
                // ref 继续查找
                return findMatchId(srcData, orgPermission);
            } else {
                //匹配到
                return orgPermission.getUri();
            }
        } else {
            throw new ClassNotFoundException("找不到引用的权限");
        }
    }

    /**
     * 通配符匹配
     *
     * @param pattern
     * @param str
     * @return
     */
    private static boolean match(String pattern, String str) {
        if (pattern == null || str == null) {
            return false;
        }

        boolean result = false;
        char c; // 当前要匹配的字符串
        boolean beforeStar = false;
        // 是否遇到通配符*
        int back_i = 0;
        // 回溯,当遇到通配符时,匹配不成功则回溯
        int back_j = 0;
        int i, j;
        for (i = 0, j = 0; i < str.length(); ) {
            if (pattern.length() <= j) {
                if (back_i != 0) {
                    // 有通配符,但是匹配未成功,回溯
                    beforeStar = true;
                    i = back_i;
                    j = back_j;
                    back_i = 0;
                    back_j = 0;
                    continue;
                }
                break;
            }

            if ((c = pattern.charAt(j)) == '*') {
                if (j == pattern.length() - 1) {
                    // 通配符已经在末尾,返回true
                    result = true;
                    break;
                }
                beforeStar = true;
                j++;
                continue;
            }

            if (beforeStar) {
                if (str.charAt(i) == c) {
                    beforeStar = false;
                    back_i = i + 1;
                    back_j = j;
                    j++;
                }
            } else {
                if (c != '?' && c != str.charAt(i)) {
                    result = false;
                    if (back_i != 0) {
                        // 有通配符,但是匹配未成功,回溯
                        beforeStar = true;
                        i = back_i;
                        j = back_j;
                        back_i = 0;
                        back_j = 0;
                        continue;
                    }
                    break;
                }
                j++;
            }
            i++;
        }

        if (i == str.length() && j == pattern.length()) {
            // 全部遍历完毕
            result = true;
        }
        return result;
    }

}
