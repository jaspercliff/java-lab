package com.jasper.storage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jasper.storage.entity.Storage;
import com.jasper.storage.mapper.StorageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageMapper storageMapper;

    @Transactional
    public void deduct(String commodityCode, int count) {
        log.info("Deducting stock: commodityCode={}, count={}", commodityCode, count);
        Storage storage = storageMapper.selectOne(new LambdaQueryWrapper<Storage>().eq(Storage::getCommodityCode, commodityCode));
        if (storage == null) {
            throw new RuntimeException("Storage not found for commodityCode: " + commodityCode);
        }
        if (storage.getCount() < count) {
            throw new RuntimeException("Insufficient stock");
        }
        storage.setCount(storage.getCount() - count);
        storageMapper.updateById(storage);
        log.info("Successfully deducted stock for commodityCode: {}", commodityCode);
    }
}
