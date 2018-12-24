package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //再redis中内容对应的key
    private static final String REDIS_CONTENT="content";

    /**
     * 新增
     *
     * @param tbContent 实体对象
     */
    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        //更新分类对应的redis缓存
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 将分类id在redis中对应的内容列表删除
     * @param categoryId 分类id
     */
    private void updateContentListInRedisByCategoryId(Long categoryId) {
        redisTemplate.boundHashOps(REDIS_CONTENT).delete(categoryId);
    }

    /**
     * 根据主键更新
     *
     * @param tbContent 实体对象
     */
    @Override
    public void update(TbContent tbContent) {

        TbContent oldContent = findOne(tbContent.getCategoryId());

        super.update(tbContent);

        if (!oldContent.getCategoryId().equals(tbContent.getCategoryId())){
            //说明分类内容被修改了，需要删除原来的缓存信息
            updateContentListInRedisByCategoryId(oldContent.getCategoryId());
        }
        //更新分类对应的内容列表
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 根据主键批量删除
     *
     * @param ids 主键集合
     */
    @Override
    public void deleteById(Serializable[] ids) {

        Example example = new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        List<TbContent> contentList = contentMapper.selectByExample(example);
        if (contentList != null && contentList.size() > 0){
            for (TbContent content : contentList){
                updateContentListInRedisByCategoryId(content.getCategoryId());
            }
        }
        //删除内容
        super.deleteById(ids);
    }

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据内容分类 id 查询启用的内容列表并降序排序
     * @param categoryId 内容分类 id
     * @return 内容列表
     */
    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> contentList = null;

        try {
            contentList = (List<TbContent>) redisTemplate.boundHashOps(REDIS_CONTENT).get(categoryId);
            if (contentList != null && contentList.size() > 0) {
                return contentList;
            }
        }catch (Exception e){
            e.printStackTrace();
       }

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId",categoryId);
        //启用状态
        criteria.andEqualTo("status","1");

        //降序排序
        example.orderBy("sortOrder").desc();


       contentList = contentMapper.selectByExample(example);

       try {
           //设置某个分类的广告到redis中
           redisTemplate.boundHashOps(REDIS_CONTENT).put(categoryId,contentList);
       }catch (Exception e){
           e.printStackTrace();
       }

        return contentList;
    }
}
