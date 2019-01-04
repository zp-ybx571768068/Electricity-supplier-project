package cn.itcast.springboot;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zp
 *  引导类
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        //启动的时候不加载横幅
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }
}
