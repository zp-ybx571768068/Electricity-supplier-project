import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    //测试字符串
    @Test
    public void testString(){
        redisTemplate.boundValueOps("string_key").set("传智播客");
        Object stringKey = redisTemplate.boundValueOps("string_key").get();
        System.out.println(stringKey);
    }

    //测试散列hash
    @Test
    public void testHash(){
        redisTemplate.boundHashOps("hash").put("key","zhi");
        redisTemplate.boundHashOps("hash").put("k1","z2");
        Set hasg = redisTemplate.boundHashOps("hash").keys();
        System.out.println(hasg);

    }
}
