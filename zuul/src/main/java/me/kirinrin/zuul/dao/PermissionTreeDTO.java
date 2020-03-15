package me.kirinrin.zuul.dao;

import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 * @Classname PermissionTreeDTO
 * @Description 用于URI查询的树状结构, 以便进行URI到action的查询
 *  get@qc/data-set/item/{id}/view-file
 *  qc
 *      -data-set
 *          -item
 *              -{id}
 *                  -view-file
 *                      -get
 *                      -post
 *
 * @Date 2020/3/15 5:46 下午
 * @Created by Kirinrin
 */
@NoArgsConstructor
@Data
@ToString
public class PermissionTreeDTO {
    /**
     *
     */
    private String uriFragment;
    private PermissionDTO permission;
    private Map<String, PermissionTreeDTO> subTree = new HashMap<String, PermissionTreeDTO>();

    /**
     *
     * ref@ 的忽略不处理
     * @param permissionList  get@qc/data-set/item/{id}/view-file
     * @return
     */
    public static Map<String, PermissionTreeDTO> buildUriTree(List<PermissionDTO> permissionList){
        Map<String, PermissionTreeDTO> tree = new HashMap<String, PermissionTreeDTO>();
        for(PermissionDTO item : permissionList) {
            String uri = item.getUri();
            String method = null;
            String[] methodSplis = item.getUri().split("@");
            if (methodSplis.length > 1) {
                uri = methodSplis[1];
                method = methodSplis[0];
                if("ref".equals(method)){
                    continue;
                }
            }


            Map<String, PermissionTreeDTO> tempTree = tree;
            Queue<String> uriQueue = splitUri2Queue(uri);
            PermissionTreeDTO createdNode = new PermissionTreeDTO();
            while (!uriQueue.isEmpty()){
                String subUri = uriQueue.poll();
                // 查找
                if (tempTree.containsKey(subUri)) {
                    tempTree = tempTree.get(subUri).getSubTree();
                } else {
                    // 创建
                    createdNode = new PermissionTreeDTO();
                    createdNode.setUriFragment(subUri);
                    tempTree.put(subUri, createdNode);
                    tempTree = createdNode.getSubTree();
                }

            }

            // 所有 URI 段都创建完成
            PermissionTreeDTO methodNode = new PermissionTreeDTO();
            methodNode.setUriFragment(method);
            methodNode.setPermission(item);
            createdNode.getSubTree().put(method, methodNode);


        }
        return tree;
    }


    /**
     * 查询PermissionTree结构，找到与用户实际调用的URI匹配的ID
     * @param uri eg "get@qc/data-set/item/134-148-3-34334442499x/view-file"
     * @return
     */
    public static PermissionTreeDTO findMatchUri(Map<String, PermissionTreeDTO> tree, String uri){
        return null;
    }


    /**
     * 分割URI 到队列
     * @param uri
     */
    public static Queue<String>  splitUri2Queue(String uri) {
        List x = Arrays.stream(uri.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        Queue<String> queue = new LinkedList<>(x);
        return queue;
    }
}
