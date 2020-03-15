package me.kirinrin.zuul;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname TokenService
 * @Description 处理相关token的读写与认证权限
 * @Date 2020/3/12 2:27 下午
 * @Created by Kirinrin
 */
@Service
@Slf4j
public class TokenAuthorityService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate redisTemplate;
    public String getTokenData(String tokenString) {
        return "";
    }

    /**
     * 验证是否有访问该URI的权限
     * 参考现在的分段方案一般是有3段，要兼容 * ｜ cas:* | cas:*:Describe*
     * 以及allow deny两种条件
     * @param token
     * @param uri
     */
    public boolean validAuthoriy(String token, String uri){
        List<PolicyPo> policys = findUserPolicy(token);
        String permissionKey = findPermissionKey(uri);

        //TODO 检察key是否满足policy的描述
        // 将 policy 与 permissionKey 逐条对比
        // 先计算 deny 再 allow
        String[] permissionFragments = permissionKey.split(":");
        for(PolicyPo policy : policys){
            if ("deny".equals( policy.getEffect())){
                String[] policyFragments = policy.getAction().split(":");
                if (isMatch(policyFragments, permissionFragments )){
                    return false;
                }
            }
        }
        for(PolicyPo policy : policys){
            if ("allow".equals( policy.getEffect())){
                String[] policyFragments = policy.getAction().split(":");
                if (isMatch(policyFragments, permissionFragments)){
                    return true;
                }
            }
        }

        return false;
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
    private List<PolicyPo> findUserPolicy(String token){
        //TODO 找到用户的权限描述语句
        return null;
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
        int back_i = 0;
        // 回溯,当遇到通配符时,匹配不成功则回溯
        int back_j = 0;
        int i, j;
        for (i = 0, j = 0; i < str.length();) {
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
