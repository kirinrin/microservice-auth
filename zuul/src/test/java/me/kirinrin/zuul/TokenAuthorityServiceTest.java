package me.kirinrin.zuul;

import cn.hutool.json.JSONObject;
import com.google.gson.JsonObject;
import me.kirinrin.zuul.dao.PermissionMatchDTO;
import me.kirinrin.zuul.dao.PermissionSetDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Classname TokenAuthorityServiceTest
 * @Description 测试权限服务类
 * @Date 2020/3/12 3:59 下午
 * @author by Kirinrin
 */
@SpringBootTest
class TokenAuthorityServiceTest {

    @Autowired TokenAuthorityService service = new TokenAuthorityService();

    @Test
    void getTokenData() {
//        security:access_token:xiapengtao:GlFQSFUPAkFMCQ4HVxUAVwMLHwkACVoaTwtWEVwKUBIEVwwH

        JSONObject data = service.getTokenData("xiapengtao:SVxQE1dWVENMVwwBBRUACgIDHgAGBVIYSl4EQwZXUU0CCVgA");
        assertNotNull(data);
        System.out.println(data);
        assertEquals("suplus", data.get("tenantId"));
        System.out.println(data.get("tenantId"));
    }
    @Test
    void testGetUriReList(){
        List<PermissionSetDTO> result = service.getUriReList();
        assertFalse(result.isEmpty());
        result.forEach( v -> System.out.println(v));
    }

    @Test
    void validAuthoriy() {
        String token = "Tw8CFAYNX0RMVg0AUxUABwdeHgkEAFYfTllWFFdXA0VTDA8F";
        String uri = "get@qc/auto-qc-job/342948712947297/export-result-detail";
        boolean can = service.validAuthoriy(service.getTokenData(token), uri);
        assertTrue(can);

         uri = "get@qc/auto-qc-job/342948712947297/export-result-detail/showData";
        can = service.validAuthoriy(service.getTokenData(token), uri);
        assertFalse(can);
    }

    @Test
    void splitCamelString() {
        String [] results = service.splitCamelString("CreateFileInstance");
        assertNotNull(results);
        assertEquals(3, results.length);
        assertEquals("Create", results[0]);
        assertEquals("File", results[1]);
        assertEquals("Instance", results[2]);
    }

    @Test
    void testSplitFlag(){
        String x = "cas:dataset:Describe*";
        String flags[] = x.split(":");
        assertNotNull(flags);
        assertEquals(flags.length, 3);
        assertEquals("cas", flags[0]);

        String star = "*";
        flags = star.split(":");
        assertNotNull(flags);
        assertEquals(flags.length, 1);
        assertEquals("*", flags[0]);
    }

    @Test
    void testIsMatch(){
        String[] policy = "*".split(":");
        String[] permission = "cas:media-player:DescribeQcTaskInstanceResult".split(":");
        assertTrue(service.isMatch(policy, permission), "match *" );

        policy = "cas:*".split(":");
        assertTrue(service.isMatch(policy, permission), "match cas:*");

        policy = "cas:dataset:*".split(":");
        assertFalse(service.isMatch(policy, permission), "not match cas:dataset:*");

        policy = "cas:media-player:*".split(":");
        assertTrue(service.isMatch(policy, permission), "match cas:media-player:*");

        policy = "cas:media-player:Describe*".split(":");
        assertTrue(service.isMatch(policy, permission), "match cas:media-player:Describe*");

        policy = "cas:media-player:*Qc*".split(":");
        assertTrue(service.isMatch(policy, permission), "match cas:media-player:*Qc*");

        policy = "cas:media-player:Qc*".split(":");
        assertFalse(service.isMatch(policy, permission), "match cas:media-player:Qc*");

        policy = "cs:c*".split(":");
        assertFalse(service.isMatch(policy, permission), "match cs:*");
    }

    @Test
    void testMatch(){
        assertTrue(service.match("Describe", "Describe"));
        assertTrue(service.match("Describe*", "DescribeXDEExx"));
        assertFalse(service.match("Desccc*", "Desxccc"));
    }
}