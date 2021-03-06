package com.xiaobudian.yamikitchen.service;

import com.xiaobudian.yamikitchen.common.LocalizedMessageSource;
import com.xiaobudian.yamikitchen.common.Util;
import com.xiaobudian.yamikitchen.domain.member.Bank;
import com.xiaobudian.yamikitchen.domain.member.BankCard;
import com.xiaobudian.yamikitchen.domain.member.RegistrationPostHandler;
import com.xiaobudian.yamikitchen.domain.member.User;
import com.xiaobudian.yamikitchen.domain.merchant.UserAddress;
import com.xiaobudian.yamikitchen.repository.account.BankRepository;
import com.xiaobudian.yamikitchen.repository.member.BankCardRepository;
import com.xiaobudian.yamikitchen.repository.member.UserAddressRepository;
import com.xiaobudian.yamikitchen.repository.member.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Johnson on 2015/4/23.
 */
@Service(value = "memberService")
@Transactional
public class MemberServiceImpl implements MemberService {
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private UserRepository userRepository;
    @Inject
    private BankRepository bankRepository;
    @Inject
    private LocalizedMessageSource localizedMessageSource;
    @Inject
    private UserAddressRepository userAddressRepository;
    @Inject
    private RegistrationPostHandler registrationPostHandler;
    @Inject
    private BankCardRepository bankCardRepository;

    @Override
    public User register(User user) {
        if (StringUtils.isEmpty(user.getNickName())) {
            String nickNamePrefix = localizedMessageSource.getMessage("user.name.default");
            user.setNickName(nickNamePrefix + StringUtils.substring(user.getUsername(), user.getUsername().length() - 4));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setBindingPhone(user.getUsername());
        User newUser = userRepository.save(user);
        registrationPostHandler.handle(newUser);
        return newUser;
    }

    @Override
    public User getUserBy(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public User getUserBy(long id) {
        return userRepository.findOne(id);
    }

    @Override
    public User changePassword(User user) {
        User u = userRepository.findByUsername(user.getUsername());
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(u);
    }

    @Override
    public List<UserAddress> getAddresses(Long uid) {
        return userAddressRepository.findByUid(uid);
    }

    @Override
    public UserAddress addAddress(UserAddress userAddress) {
        return userAddressRepository.save(userAddress);
    }

    @Override
    public User getUser(Long creator) {
        return userRepository.findOne(creator);
    }

    @Override
    public boolean removeAddress(Long addressId) {
        userAddressRepository.delete(addressId);
        return true;
    }

    @Override
    public User updateUser(User user) {
        User u = userRepository.findOne(user.getId());
        BeanUtils.copyProperties(user, u, Util.getNullPropertyNames(user));
        return userRepository.save(u);
    }

    @Override
    public BankCard bindingBankCard(BankCard card) {
        BankCard c = bankCardRepository.findByUid(card.getUid());
        if (c != null) bankCardRepository.delete(c);
        return bankCardRepository.save(card);
    }

    @Override
    public BankCard getBindingBankCard(Long uid) {
        return bankCardRepository.findByUid(uid);
    }

    @Override
    public Bank getBankByName(String bankName) {
        return bankRepository.findByBankName(bankName);
    }

    @Override
    public Bank getBankByBinCode(String binCode) {
        return bankRepository.findByBinCode(binCode);
    }
}
