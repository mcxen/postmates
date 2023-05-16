package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcxgroup.postmates.entity.AddressBook;
import com.mcxgroup.postmates.mapper.AddressBookMapper;
import com.mcxgroup.postmates.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @Description: 地址服务层实现类
 * @author: MCXEN
 * @date: 2022/11/30
 * MCXEN
 */

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
