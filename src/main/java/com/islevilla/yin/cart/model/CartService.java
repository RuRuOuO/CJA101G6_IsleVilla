package com.islevilla.yin.cart.model;

import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import redis.clients.jedis.Jedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.islevilla.yin.productphoto.ProductPhotoService;
import com.islevilla.yin.productphoto.ProductPhoto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPhotoService productPhotoService;

    private String getCartKey(String userId) {
        return "cart:" + userId;
    }

    public void addToCart(String userId, Integer productId, Integer quantity) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String cartKey = getCartKey(userId);
            jedis.hincrBy(cartKey, productId.toString(), quantity);
        }
    }

    public List<CartDTO> getCartItems(String userId) {
        String cartKey = getCartKey(userId);
        List<CartDTO> cartItems = new ArrayList<>();
        int totalAmount = 0;

        try (Jedis jedis = new Jedis("localhost", 6379)) {
            Map<String, String> redisCart = jedis.hgetAll(cartKey);

            for (Map.Entry<String, String> entry : redisCart.entrySet()) {
                Integer productId = Integer.parseInt(entry.getKey());
                Integer quantity = Integer.parseInt(entry.getValue());
                Product product = productService.getProductById(productId);

                if (product != null) {
                    int subtotal = product.getProductPrice() * quantity;
                    totalAmount += subtotal;
                    String imageUrl = "/img/product/default.png";
                    ProductPhoto photo = productPhotoService.getFirstProductPhotoByProductId(productId);
                    if (photo != null) {
                        imageUrl = "/product/photo/" + productId;
                    }
                    cartItems.add(new CartDTO(productId, product.getProductName(),
                            product.getProductPrice(), quantity, subtotal, imageUrl, product.getProductQuantity()));
                }
            }
        }
        return cartItems;
    }

    public void updateQuantity(String userId, Integer productId, Integer quantity) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String cartKey = getCartKey(userId);
            if (quantity <= 0) {
                jedis.hdel(cartKey, productId.toString());
            } else {
                jedis.hset(cartKey, productId.toString(), quantity.toString());
            }
        }
    }

    public void removeFromCart(String userId, Integer productId) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String cartKey = getCartKey(userId);
            jedis.hdel(cartKey, productId.toString());
        }
    }

    public void clearCart(String userId) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String cartKey = getCartKey(userId);
            jedis.del(cartKey);
        }
    }

    public int getProductQuantityInCart(String userId, Integer productId) {
        String cartKey = getCartKey(userId);
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String value = jedis.hget(cartKey, productId.toString());
            if (value != null) {
                return Integer.parseInt(value);
            }
        }
        return 0;
    }
}
