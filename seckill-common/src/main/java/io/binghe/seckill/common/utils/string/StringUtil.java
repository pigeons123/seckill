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
package io.binghe.seckill.common.utils.string;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description 字符串工具类
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
public class StringUtil {

    public static String append(Object ... params){
        if (params == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length - 1; i++){
            sb.append(params[i]).append("_");
        }
        sb.append(params[params.length - 1]);
        return sb.toString();
    }

}
