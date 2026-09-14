package com.jasper.account.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jasper.account.entity.Account;
import com.jasper.account.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;

    @Transactional
    public void debit(String userId, int money) {
        log.info("Deducting money: userId={}, money={}", userId, money);
        Account account = accountMapper.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getUserId, userId));
        if (account == null) {
            throw new RuntimeException("Account not found for userId: " + userId);
        }
        if (account.getMoney() < money) {
            throw new RuntimeException("Insufficient balance");
        }
        account.setMoney(account.getMoney() - money);
        accountMapper.updateById(account);
        log.info("Successfully deducted money for userId: {}", userId);
    }
}
