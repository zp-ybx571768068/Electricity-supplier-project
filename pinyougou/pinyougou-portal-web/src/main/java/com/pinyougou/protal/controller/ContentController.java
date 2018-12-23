package com.pinyougou.protal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zp
 */
@RequestMapping("content")
@RestController
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 根据内容分类 id 查询启用的内容列表并降序排序
     * @param categoryId 内容分类 id
     * @return 内容列表
     * findContentListByCategoryId
     */
    @GetMapping("/findContentListByCategoryId")
    public List<TbContent> findContentListByCategoryId(Long categoryId){
        return contentService.findContentListByCategoryId(categoryId);
    }

}
