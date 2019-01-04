package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zp
 */
@RequestMapping("/address")
@RestController
public class AddressController {

    @Reference
    private AddressService addressService;

    @GetMapping("/findAddressList")
    public List<TbAddress> findAddressList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbAddress tbAddress = new TbAddress();
        tbAddress.setUserId(username);
        return addressService.findByWhere(tbAddress);
    }
    @RequestMapping("/findAll")
    public List<TbAddress> findAll() {
        return addressService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return addressService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbAddress address) {
        try {
            addressService.add(address);
            return Result.success("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("增加失败");
    }

    @GetMapping("/findOne")
    public TbAddress findOne(Long id) {
        return addressService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbAddress address) {
        try {
            addressService.update(address);
            return Result.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            addressService.deleteById(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("删除失败");
    }

    /**
     * 分页查询列表
     * @param address 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbAddress address, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return addressService.search(page, rows, address);
    }

}
