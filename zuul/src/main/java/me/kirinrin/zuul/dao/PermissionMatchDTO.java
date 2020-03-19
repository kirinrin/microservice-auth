package me.kirinrin.zuul.dao;

import lombok.*;

import java.util.*;
import java.util.regex.Pattern;
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
@AllArgsConstructor
@ToString
public class PermissionMatchDTO {
    /**
     *
     */
    private String uriMatchRe;
    private PermissionDTO permission;


    /**
     * 查询PermissionTree结构，找到与用户实际调用的URI匹配的ID
     * @param uri eg "get@qc/data-set/item/134-148-3-34334442499x/view-file"
     * @return
     */
    public static PermissionMatchDTO findMatchUri(List<PermissionMatchDTO> list, String uri){
        for (PermissionMatchDTO value : list) {
            if (Pattern.matches(value.getUriMatchRe(), uri)){
                return value;
            }
        }
        return null;
    }

}
