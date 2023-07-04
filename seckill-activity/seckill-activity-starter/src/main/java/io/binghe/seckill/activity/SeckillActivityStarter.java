/**
 * Copyright 2022-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.binghe.seckill.activity;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description 项目启动类
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
@EnableDubbo
@SpringBootApplication
public class SeckillActivityStarter {

    public static void main(String[] args) {
        System.setProperty("user.home", "/home/binghe/activity");
        SpringApplication.run(SeckillActivityStarter.class, args);
    }

}
