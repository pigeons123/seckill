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
package io.binghe.seckill.order.application.place.impl;

import com.alibaba.fastjson.JSONObject;
import io.binghe.seckill.common.cache.distribute.DistributedCacheService;
import io.binghe.seckill.common.constants.SeckillConstants;
import io.binghe.seckill.common.exception.ErrorCode;
import io.binghe.seckill.common.exception.SeckillException;
import io.binghe.seckill.common.lock.DistributedLock;
import io.binghe.seckill.common.lock.factoty.DistributedLockFactory;
import io.binghe.seckill.common.model.dto.SeckillGoodsDTO;
import io.binghe.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import io.binghe.seckill.order.application.command.SeckillOrderCommand;
import io.binghe.seckill.order.application.place.SeckillPlaceOrderService;
import io.binghe.seckill.order.domain.model.entity.SeckillOrder;
import io.binghe.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description 分布式锁下单
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lock")
public class SeckillPlaceOrderLockService implements SeckillPlaceOrderService {
    private final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLockService.class);
    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;
    @Autowired
    private DistributedLockFactory distributedLockFactory;
    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        //获取商品
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        //检测商品信息
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        String lockKey = SeckillConstants.getKey(SeckillConstants.ORDER_LOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);
        // 获取内存中的库存信息
        String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        //是否扣减了缓存中的库存
        boolean isDecrementCacheStock = false;
        try {
            //未获取到分布式锁
            if (!lock.tryLock(2, 5, TimeUnit.SECONDS)){
                throw new SeckillException(ErrorCode.RETRY_LATER);
            }
            // 查询库存信息
            Integer stock = distributedCacheService.getObject(key, Integer.class);
            //库存不足
            if (stock < seckillOrderCommand.getQuantity()){
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
            //扣减库存
            distributedCacheService.decrement(key, seckillOrderCommand.getQuantity());
            //正常执行了扣减缓存中库存的操作
            isDecrementCacheStock = true;
            //构建订单
            SeckillOrder seckillOrder = this.buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            //保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            //扣减数据库库存
            seckillGoodsDubboService.updateDbAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId());
            //库存数据库库存
            return seckillOrder.getId();
        } catch (Exception e) {
            //已经扣减了缓存中的库存，则需要增加回来
            if (isDecrementCacheStock){
                distributedCacheService.increment(key, seckillOrderCommand.getQuantity());
            }
            if (e instanceof InterruptedException){
                logger.error("SeckillPlaceOrderLockService|下单分布式锁被中断|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
            }else{
                logger.error("SeckillPlaceOrderLockService|分布式锁下单失败|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
            }
            throw new SeckillException(e.getMessage());
        }finally {
            lock.unlock();
        }
    }

}