package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.entity.AddressBook;
import com.mcxgroup.postmates.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    @Transactional
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("录入AddressBook{}",addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }
    @PutMapping("/default")
    @Transactional
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("当前的地址为：{},要设计为默认",addressBook);
        AddressBook defaultNew = addressBookService.getById(addressBook.getId());
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.eq(AddressBook::getIsDefault,1);
        AddressBook defaultOld = addressBookService.getOne(wrapper);
        if (defaultOld != null) {
            defaultOld.setIsDefault(0);
            addressBookService.updateById(defaultOld);
        }
        defaultNew.setIsDefault(1);
        addressBookService.updateById(defaultNew);
        return R.success(addressBook);
    }
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook!=null){
            return R.success(addressBook);
        }else
            return R.error("无法找到该id");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.eq(AddressBook::getIsDefault,1);
        AddressBook defaultOld = addressBookService.getOne(wrapper);
        if (defaultOld == null) {
            return R.error("无法找到default");
        }else
            return R.success(defaultOld);
    }
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(null!=addressBook.getUserId(),AddressBook::getUserId,addressBook.getUserId());
        wrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(wrapper);
        return R.success(list);
    }
}
