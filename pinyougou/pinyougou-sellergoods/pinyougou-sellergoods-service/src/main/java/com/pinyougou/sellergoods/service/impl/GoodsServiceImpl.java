package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.mapper.weekend.Weekend;

import javax.lang.model.element.NestingKind;
import java.util.*;

/**
 * @author zp
 */
@Transactional
@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);


        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //不查询已删除商品
        criteria.andNotEqualTo("isDelete","1");

        if (!StringUtil.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId",goods.getSellerId());
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus", goods.getAuditStatus());
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }


        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
       //保存商品基本信息
        add(goods.getGoods());
        //保存商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        //保存商品sku列表信息
        saveItemList(goods);
    }

    @Override
    public Goods findGoodsById(Long id) {
        return findGoodsByIdAndStatus(id,null);
    }

    @Override
    public void updateGoods(Goods goods) {
        //修改过重新设置为未审核状态
        goods.getGoods().setAuditStatus("0");

        //更新商品基本信息
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
        //更新商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //先删除sku列表再进行添加，实现更新操作
        TbItem pram = new TbItem();
        pram.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(pram);

        saveItemList(goods);

    }

    @Override
    public Result updateStatus(Long[] ids, String status) {

        try {
            Example goodsExample = new Example(TbGoods.class);
            goodsExample.createCriteria().andIn("id",Arrays.asList(ids));
            List<TbGoods> goodsList = goodsMapper.selectByExample(goodsExample);
            for (TbGoods tbGoods : goodsList){
                if ("0".equals(tbGoods.getAuditStatus())){
                    return Result.error("商家未提交审核，审核失败");
                }else {
                    TbGoods goods =new TbGoods();
                    goods.setAuditStatus(status);

                    Example example =new Example(TbGoods.class);
                    example.createCriteria().andIn("id",Arrays.asList(ids));
                    goodsMapper.updateByExampleSelective(goods,example);

                    if ("2".equals(status)){
                        TbItem item = new TbItem();
                        item.setStatus("1");

                        Example itemExample = new Example(TbItem.class);
                        itemExample.createCriteria().andIn("goodsId",Arrays.asList(ids));

                        itemMapper.updateByExampleSelective(item,itemExample);
                    }
                    return Result.success("商品审核成功");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("商品审核失败");
    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete("1");

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }

    /**
     *  商品上下架功能
     * @param ids 要上下架的商品id数组
     * @param marketable 更改的上下架的状态值
     * @return 成功还是失败
     */
    @Override
    public Result isPutAway(Long[] ids, String marketable) {
        String message = null;
        try {
            if ("1".equals(marketable)){
                message = "上架";
            }
            if ("2".equals(marketable)){
                message = "下架";
            }
            Example goodsExample = new Example(TbGoods.class);
            goodsExample.createCriteria().andIn("id",Arrays.asList(ids));
            List<TbGoods> goodsList = goodsMapper.selectByExample(goodsExample);
            for (TbGoods tbGoods : goodsList){

                if ("2".equals(tbGoods.getAuditStatus())){
                    TbGoods goods = new TbGoods();
                    goods.setIsMarketable(marketable);

                    Example example = new Example(TbGoods.class);
                    example.createCriteria().andIn("id",Arrays.asList(ids));
                    goodsMapper.updateByExampleSelective(goods , example);
                    return Result.success(message+"成功");
                }else {
                    return Result.error("商品未审核");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error(message+"失败");
    }

    /**
     *  提交审核
     * @param ids
     * @param status
     */
    @Override
    public void submitAudit(Long[] ids, String status) {
        TbGoods goods =new TbGoods();
        goods.setAuditStatus(status);

        Example example =new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(goods,example);

        if ("2".equals(status)) {
            TbItem item = new TbItem();
            item.setStatus("1");

            Example itemExample = new Example(TbItem.class);
            itemExample.createCriteria().andIn("goodsId", Arrays.asList(ids));

            itemMapper.updateByExampleSelective(item, itemExample);
        }
}

    /**
     *  根据SPU id集合和状态查询这些商品对应的sku列表
     * @param ids
     * @param status
     * @return
     */
    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status) {
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("status",status).andIn("goodsId",Arrays.asList(ids));
        return itemMapper.selectByExample(example);
    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String status) {

        Goods goods = new Goods();
        //查询商品spu
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        goods.setGoods(tbGoods);

        //查询商品描述
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        goods.setGoodsDesc(tbGoodsDesc);


        //查询商品sku
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId",goodsId);
        if (!StringUtils.isEmpty(status)){
            criteria.andEqualTo("status",status);
        }
        //按照是否默认值降序排序
        example.orderBy("isDefault").desc();

        List<TbItem> items = itemMapper.selectByExample(example);
        goods.setItemList(items);
        return goods;
    }


    /**
     * 保存商品sku列表
     * @param goods 商品信息（基本、描述、sku列表）
     */
    private void saveItemList(Goods goods){
        //是否启用了规格
        if ("1".equals(goods.getGoods().getIsEnableSpec())){
            //启用规格
            if (goods.getItemList() != null && goods.getItemList().size() > 0){
                for (TbItem item : goods.getItemList()){

                    //sku标题 = spu的名称+规格的名称拼接
                    String title = goods.getGoods().getGoodsName();
                    //获取sku规格
                    Map<String,String> map = JSONArray.parseObject(item.getSpec(),Map.class);
                    Set<Map.Entry<String,String>> entries = map.entrySet();
                    for (Map.Entry<String,String> entry :entries){
                        title += ""+entry.getValue();
                    }
                    item.setTitle(title);

                    setItemValue(goods,item);

                    //保存sku
                    itemMapper.insertSelective(item);
                }
            }
        }else {
            //不启用规格
            TbItem tbItem = new TbItem();

            //标题
            tbItem.setTitle(goods.getGoods().getGoodsName());
            //spec
            tbItem.setSpec("{}");
            //价格使用spu
            tbItem.setPrice(goods.getGoods().getPrice());
            //库存量9999
            tbItem.setNum(9999);
            //未启用，0
            tbItem.setStatus("0");
            //是否默认，因为只有一个sku，所以肯定是默认的
            tbItem.setIsDefault("1");

            setItemValue(goods, tbItem);

            itemMapper.insertSelective(tbItem);
        }

    }

    /**
     * 设置sku的值
     * @param goods 商品信息（基本、描述、sku列表）
     * @param item sku
     */
    private void setItemValue(Goods goods,TbItem item){
        item.setGoodsId(goods.getGoods().getId());
        //sku的商品分类 = spu的第三级目录
        item.setCategoryid(goods.getGoods().getCategory3Id());
        //商品分类中文名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
        item.setCategory(itemCat.getName());

        //图片
        List<Map> imgList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
        if (imgList != null && imgList.size() > 0){
            //将商品的第一张图作为sku图片
            item.setImage(imgList.get(0).get("url").toString());
        }

        //商家id
        item.setSellerId(goods.getGoods().getSellerId());
        TbSeller seller = sellerMapper.selectByPrimaryKey(item.getSellerId());
        item.setSeller(seller.getName());

        //创建时间
        item.setCreateTime(new Date());
        //更新时间
        item.setUpdateTime(item.getCreateTime());

        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());

    }
}
