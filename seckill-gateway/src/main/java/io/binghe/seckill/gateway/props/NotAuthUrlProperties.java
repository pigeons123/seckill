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
package io.binghe.seckill.gateway.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description NotAuthUrlProperties
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
@Component
@ConfigurationProperties("seckill.gateway.auth")
public class NotAuthUrlProperties {

    private LinkedHashSet<String> shouldSkipUrls;

    public LinkedHashSet<String> getShouldSkipUrls() {
        return shouldSkipUrls;
    }

    public void setShouldSkipUrls(LinkedHashSet<String> shouldSkipUrls) {
        this.shouldSkipUrls = shouldSkipUrls;
    }
}
