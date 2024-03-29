package fun.johntaylor.kunkka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fun.johntaylor.kunkka.entity.oj.Oj;
import fun.johntaylor.kunkka.service.oj.OjService;

@SpringBootTest(classes = KunkkaApplication.class)
public class KunkkaApplicationTests {
    @Autowired
    OjService ojService;

    @Test
    public void testTransaction() {
        Oj oj = new Oj();
        oj.setPid(11L);
        oj.setUid(1L);
        oj.setName("test");
        oj.setDifficulty("困难");
        oj.setOjType(1);
        oj.setStandalone("是");
        oj.setStudy("是");
        oj.setType("二分搜索");
        oj.setPreTime(System.currentTimeMillis());
        oj.setLink("test/");
        oj.setCreateTime(System.currentTimeMillis());
        oj.setStatus(1);
        ojService.add(oj);
    }

}
