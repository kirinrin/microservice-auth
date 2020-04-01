package me.kirinrin.zuul;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.kirinrin.zuul.dao.PermissionMatchDTO;
import me.kirinrin.zuul.dao.PermissionSetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @Classname TokenService
 * @Description 处理相关token的读写与认证权限
 * @Date 2020/3/12 2:27 下午
 * @author by Kirinrin
 */
@Service
@Slf4j
public class TokenAuthorityService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate redisTemplate;

    static List<PermissionSetDTO> uriReList = null;

    public List<PermissionSetDTO>  getUriReList (){
        if(uriReList == null){
            log.info("初始化uriReList");
            List<PermissionSetDTO> permissionList = new ArrayList<>();
            Set<String> keys = stringRedisTemplate.keys("security:permission-map:*");
            for (String key : keys) {
                log.info("security:permission-map* = {}", key);
                Set<String> sets = stringRedisTemplate.boundSetOps(key).members();
                PermissionSetDTO p = new PermissionSetDTO();
                p.setKey(key);
                p.setValueIds(sets);
                p.setUri(key.substring(24));
                p.setUriRe(p.getUri().replaceAll("\\{[A-Za-z0-9_]+\\}", "[A-Za-z0-9_@\\.-\\*]+"));
                permissionList.add(p);
            }
            uriReList = permissionList;
        }
        return uriReList;
    }

    /**
     * 从Redis中获取token
     * @param tokenString
     * @return
     */
    public JSONObject getTokenData(String tokenString) {
        String key = String.format("security:access_token:%s", tokenString);
        Set<String>keys = stringRedisTemplate.keys(key);
        if(keys.isEmpty()){
            log.debug("查询 token 无法查到");
            return null;
        }else{
            String data = null;
            for (String k : keys) {
                data = stringRedisTemplate.boundValueOps(k).get();
                stringRedisTemplate.boundValueOps(k).expire(72, TimeUnit.HOURS);
            }
            if(data == null){
                log.warn("查询 token 但无对应的值 = {}",tokenString);
            }
            log.debug("查询 token = {} data = {}",tokenString,  data);
            JSONObject jsonObject = JSONUtil.parseObj(data);
            return jsonObject;
        }

    }

    private PermissionSetDTO findMatchUri(String uri){
        log.info("尝试匹配URI 到模版 {}", uri);
        for (PermissionSetDTO reData : getUriReList()) {
            log.debug("尝试匹配URI到模版 {} - {}", uri, reData);
            if (Pattern.matches(reData.getUriRe(), uri)){
                log.debug("匹配URI到模版");
                return reData;
            }
        }

        return null;
    }

    /**
     * 验证是否有访问该URI的权限
     * 参考现在的分段方案一般是有3段，要兼容 * ｜ cas:* | cas:*:Describe*
     * 以及allow deny两种条件
     * @param tokenData
     * @param uri
     */
    public boolean validAuthoriy(JSONObject tokenData, String uri) {
        log.info("验证是否有权限可以访问URI = {} data = {}", uri, tokenData);
        JSONArray actions = tokenData.getJSONArray("actions");

        PermissionSetDTO matchUriData = findMatchUri(uri);
        log.debug("匹配Url 定义 {} - {}", uri, matchUriData);

        if (matchUriData == null) {
            log.info("无此Url权限定义 {}", uri);
            return false;
        } else {
            for (String actionId : matchUriData.getValueIds()) {
                log.debug("匹配用户权限是否有 {}", actionId);
                for (String action : actions.toList(String.class)) {
                    if (match(action, actionId)) {
                        log.debug("匹配用户权限 OK");
                        return true;
                    }
                }
            }
            log.debug("匹配用户权限，用户无权访问 uri = {}, id = {} actions = {}", uri, matchUriData.getValueIds(), actions);
            return false;
        }

    }

    /**
     * 验证单条是否满足
     * eg: cas:media-player:DescribeQcTaskInstanceResult   * | cas:*:Describe* | cas:dataset:* | cas:dataset:Describe*
     *
     * @param policyFragments  eg:   * | cas:*:Describe* | cas:dataset:* | cas:dataset:Describe*
     * @param permissionFragments eg:   cas:media-player:DescribeQcTaskInstanceResult
     * @return 返回是否符合语法约定
     */
    public boolean isMatch(String[] policyFragments, String[] permissionFragments){
        if(policyFragments == null || permissionFragments == null){
            log.warn("权限比较时发现规则错误 {}  --  {}", policyFragments, permissionFragments);
            return false;
        }
        // 循环比较发现不符合的规则就返回false

        for(int i = 0; i< policyFragments.length; i ++){
            String poFlag = policyFragments[i];
            String peFlag = permissionFragments[i];;
            if(match(poFlag, peFlag)){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }

    private String findPermissionKey(String uri){
        //TODO 把类似 /Users/xxxxx Get这类的请求，变换成 post@System/file
        //TODO CreateFileInstance: "post@system/file" 查询缓存找到对应关系
        // 返回 CreateFileInstance 这样的值
        return null;
    }
    private List<PolicyPo> findUserPolicy(JSONObject tokenData){
        List<PolicyPo> policyList = new ArrayList<PolicyPo>();
        JSONArray array = tokenData.getJSONArray("actions");
        for (Object o : array) {
            String action = (String) o;
            policyList.add(new PolicyPo(action, "allow", "*"));
        }
        return policyList;
    }

    /**
     * 切分驼峰字符串
     * @param word
     * @return
     */
    public String[] splitCamelString(String word){
        String[] words = word.split("(?<!(^|[A-Z0-9]))(?=[A-Z0-9])|(?<!(^|[^A-Z]))(?=[0-9])|(?<!(^|[^0-9]))(?=[A-Za-z])|(?<!^)(?=[A-Z][a-z])" );
        return words;
    }

    /**
     * 通配符算法。 可以匹配"*"和"?"
     * 如a*b?d可以匹配aAAAbcd
     * @param pattern 匹配表达式
     * @param str 匹配的字符串
     * @return
     */
    public boolean match(String pattern, String str) {
        if (pattern == null || str == null){
            return false;
        }

        boolean result = false;
        char c; // 当前要匹配的字符串
        boolean beforeStar = false;
        // 是否遇到通配符*
        int backI = 0;
        // 回溯,当遇到通配符时,匹配不成功则回溯
        int backJ = 0;
        int i, j;
        for (i = 0, j = 0; i < str.length();) {
            if (pattern.length() <= j) {
                if (backI != 0) {
                    // 有通配符,但是匹配未成功,回溯
                    beforeStar = true;
                    i = backI;
                    j = backJ;
                    backI = 0;
                    backJ = 0;
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
                    backI = i + 1;
                    backJ = j;
                    j++;
                }
            } else {
                if (c != '?' && c != str.charAt(i)) {
                    result = false;
                    if (backI != 0) {
                        // 有通配符,但是匹配未成功,回溯
                        beforeStar = true;
                        i = backI;
                        j = backJ;
                        backI = 0;
                        backJ = 0;
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
