package org.yyubin.gesellschaftboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Gesellschaft Boot Application
 * - scanBasePackages: infrastructure 레이어의 컴포넌트 스캔 포함
 */
@SpringBootApplication(scanBasePackages = {
    "org.yyubin.gesellschaftboot",
    "org.yyubin.gesellschaftinfrastructure"
})
public class GesellschaftBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(GesellschaftBootApplication.class, args);
    }

}
