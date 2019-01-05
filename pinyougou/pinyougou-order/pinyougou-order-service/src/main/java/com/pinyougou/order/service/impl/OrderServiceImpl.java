package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    /**
     *  在redis中购物车数据的key
     */
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;



    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     *  将购物车列表中的商品保存成订单基本、明细、支付日志信息
     * @param order 订单基本信息
     * @return 支付业务id
     */
    @Override
    public String addOrder(TbOrder order) {
        //支付日志id
        String outTradeNo = "";

        //1、获取用户对应的购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());
        if (cartList != null && cartList.size() > 0) {
            //2、遍历购物车列表的每个购物车对应生成一个订单和多个其对应的订单明细
            //本次应该支付的总金额
            double totalFee = 0.0;
            //本次交易的订单集合
            String orderIds = "";
            for (Cart cart : cartList) {
                long orderId = idWorker.nextId();
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(orderId);
                //订单来源
                tbOrder.setSourceType(order.getSourceType());
                //购买者
                tbOrder.setUserId(order.getUserId());
                //未付款
                tbOrder.setStatus("1");
                //支付类型
                tbOrder.setPaymentType(order.getPaymentType());
                //收货人手机号
                tbOrder.setReceiverMobile(order.getReceiverMobile());
                //收货人
                tbOrder.setReceiver(order.getReceiver());
                //收获地址
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());
                tbOrder.setCreateTime(new Date());
                tbOrder.setUpdateTime(tbOrder.getCreateTime());
                //卖家id
                tbOrder.setSellerId(order.getSellerId());
                //本笔订单的支付金额
                double payment = 0.0;
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(orderId);
                    payment += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insertSelective(orderItem);
                }
                tbOrder.setPayment(new BigDecimal(payment));
                orderMapper.insertSelective(tbOrder);
                //记录订单id
                if (orderIds.length() > 0 ){
                    orderIds += ","+orderId;
                }else {
                    orderIds += orderId;
                }
                //累计本次所有订单的总金额
                totalFee += payment;
            }
            //3、如果是微信支付的话则需要生成支付日志保存到数据库
            if ("1".equals(order.getPaymentType())){
                outTradeNo = idWorker.nextId()+"";
                TbPayLog tbPayLog = new TbPayLog();
                tbPayLog.setOutTradeNo(outTradeNo);
                //支付状态未支付
                tbPayLog.setTradeState("0");
                tbPayLog.setUserId(order.getUserId());
                tbPayLog.setCreateTime(new Date());
                //总金额取整
                tbPayLog.setTotalFee((long) (totalFee*100));
                //本次订单id集合
                tbPayLog.setOrderList(orderIds);
                payLogMapper.insertSelective(tbPayLog);
            }
            //4、删除用户对应的购物车列表
            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());
        }
        //5、返回支付日志 id；如果不是微信支付则返回空

        return outTradeNo;
    }
}
