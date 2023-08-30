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
package io.binghe.seckill.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description Spring事务编程配置类
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
@Configuration
@ComponentScan(value = {"io.binghe.seckill", "com.alibaba.cola"})
@ServletComponentScan(basePackages = {"io.binghe.seckill"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SeckillGatewayConfig {
    private final List<ViewResolver> viewResolvers;


    @Value("${spring.cloud.gateway.discovery.locator.route-id-prefix}")
    private String routeIdPrefix;

    public SeckillGatewayConfig(ObjectProvider<List<ViewResolver>> viewResolversProvider) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
    }

    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }

    /**
     * 初始化一个限流的过滤器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void init() {
        this.initGatewayRules();
//        this.initCustomizedApis();
        this.initBlockHandlers();
    }

    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        ApiDefinition api1 = new ApiDefinition("user_api1")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    // 以/server-user/user/api1 开头的请求
                    add(new ApiPathPredicateItem().setPattern("/seckill-user/user/api1/**").
                            setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        ApiDefinition api2 = new ApiDefinition("user_api2")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    // 以/server-user/user/api2/demo1 完成的url路径匹配
                    add(new ApiPathPredicateItem().setPattern("/seckill-user/user/api2/demo1"));
                }});
        definitions.add(api1);
        definitions.add(api2);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }
    /**
     * 配置初始化的限流参数
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();

        /**
         * Sentinel整合SpringCloud Gateway使用的API类型为Route ID类型，也就是基于route维度时，
         * 由于Sentinel为SpringCloud Gateway网关生成的API名称规则如下：
         * 生成的规则为：${spring.cloud.gateway.discovery.locator.route-id-prefix}后面直接加上目标微服务的名称，如下所示。
         * ${spring.cloud.gateway.discovery.locator.route-id-prefix}目标微服务的名称
         * 其中，${spring.cloud.gateway.discovery.locator.route-id-prefix}是在yml文件中配置的访问前缀
         *
         * 为了让通过服务网关访问目标微服务链接后，请求链路中生成的API名称与流控规则中生成的API名称一致，以达到启动项目即可实现访问链接的限流效果，
         * 而无需登录Setinel管理界面手动配置限流规则，可以将
         * resource参数设置为${spring.cloud.gateway.discovery.locator.route-id-prefix}目标微服务的名称
         *
         * 当然，如果不按照上述配置，也可以在项目启动后，通过服务网关访问目标微服务链接后，在Sentinel管理界面的请求链路中找到对应的API名称所代表的请求链路，
         * 然后手动配置限流规则。
         **/
        //用户微服务
        rules.add(this.getGatewayFlowRule(getResource("seckill-user")));
        //秒杀活动微服务
        rules.add(this.getGatewayFlowRule(getResource("seckill-activity")));
        //秒杀商品微服务
        rules.add(this.getGatewayFlowRule(getResource("seckill-goods")));
        //秒杀订单微服务
        rules.add(this.getGatewayFlowRule(getResource("seckill-order")));
        //库存微服务
        rules.add(this.getGatewayFlowRule(getResource("seckill-stock")));
        //预约微服务
        rules.add(this.getGatewayFlowRule(getResource("seckill-reservation")));
        //加载规则
        GatewayRuleManager.loadRules(rules);
    }

    private String getResource(String targetServiceName){
        if (routeIdPrefix == null){
            routeIdPrefix = "";
        }
        return routeIdPrefix.concat(targetServiceName);
    }

    private GatewayFlowRule getGatewayFlowRule(String resource){
        //传入资源名称生成GatewayFlowRule
        GatewayFlowRule gatewayFlowRule = new GatewayFlowRule(resource);
        //限流阈值
        gatewayFlowRule.setCount(100);
        //统计的时间窗口，单位为
        gatewayFlowRule.setIntervalSec(1);
        return gatewayFlowRule;
    }

    /**
     * 配置限流的异常处理器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler(ServerCodecConfigurer serverCodecConfigurer) {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * 自定义限流异常页面
     */
    private void initBlockHandlers() {
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable)-> {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 1001);
            map.put("codeMsg", "Sentinel-接口被限流了");
            return ServerResponse.status(HttpStatus.OK).
                    contentType(MediaType.APPLICATION_JSON_UTF8).
                    body(BodyInserters.fromObject(map));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}
