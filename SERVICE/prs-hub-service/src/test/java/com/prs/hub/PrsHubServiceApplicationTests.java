package com.prs.hub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrsHubServiceApplicationTests {

    @Test
    void contextLoads() {
        Float str =  Float.valueOf("5E-08").floatValue();
        System.out.println(str);
    }

}
