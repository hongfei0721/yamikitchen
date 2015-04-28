package com.xiaobudian.yamikitchen.service;

import com.xiaobudian.yamikitchen.domain.User;
import com.xiaobudian.yamikitchen.domain.merchant.Favorite;
import com.xiaobudian.yamikitchen.domain.merchant.FavoriteResult;
import com.xiaobudian.yamikitchen.domain.merchant.Merchant;
import com.xiaobudian.yamikitchen.domain.merchant.Product;
import com.xiaobudian.yamikitchen.repository.FavoriteRepository;
import com.xiaobudian.yamikitchen.repository.MerchantRepository;
import com.xiaobudian.yamikitchen.repository.ProductRepository;
import com.xiaobudian.yamikitchen.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Johnson on 2015/4/24.
 */
@Service(value = "merchantService")
public class MerchantServiceImpl implements MerchantService {
    @Inject
    private MerchantRepository merchantRepository;
    @Inject
    private ProductRepository productRepository;
    @Inject
    private FavoriteRepository favoriteRepository;
    @Inject
    private UserRepository userRepository;

    @Override
    public List<Merchant> getMerchants(int page, int pageSize, Double longitude, Double latitude) {
        return merchantRepository.findByLongitudeAndLatitude(longitude, latitude, new PageRequest(page, pageSize));
    }

    @Override
    public List<Product> getProductsBy(Long rid, Integer page, Integer size) {
        return productRepository.findByMerchantId(rid, new PageRequest(page, size));
    }

    @Override
    public FavoriteResult addFavorite(Long rid, Long uid) {
        Favorite favorite = favoriteRepository.findByMerchantIdAndUid(rid, uid);
        if (favorite != null) return null;
        Favorite result = new Favorite(rid, uid);
        User user = userRepository.findOne(uid);
        result.setNickName(user.getNickName());
        result.setHeadPic(user.getHeadPic());
        favoriteRepository.save(result);
        Merchant merchant = merchantRepository.findOne(rid);
        merchant.setFavoriteCount(merchant.getFavoriteCount() + 1);
        merchantRepository.save(merchant);
        return new FavoriteResult(rid, true);
    }

    @Override
    public FavoriteResult removeFavorite(Long rid, Long uid) {
        Favorite favorite = favoriteRepository.findByMerchantIdAndUid(rid, uid);
        if (favorite == null) return new FavoriteResult(rid, true);
        favoriteRepository.delete(favorite);
        Merchant merchant = merchantRepository.findOne(rid);
        merchant.setFavoriteCount(merchant.getFavoriteCount() - 1);
        merchantRepository.save(merchant);
        return new FavoriteResult(rid, false);
    }

    @Override
    public List<Merchant> getFavorites(Long uid, Integer page, Integer size) {
        return merchantRepository.findByUidFavorite(uid, new PageRequest(page, size));
    }

    @Override
    public List<Product> gteMainProduct(Long rid) {
        return productRepository.findByMerchantIdAndMainIsTrue(rid);
    }

    @Override
    public Merchant getMerchantBy(Long rid) {
        return merchantRepository.findOne(rid);
    }
}
