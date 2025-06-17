package com.islevilla.yin.cart;
import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.*;

@Controller
public class CartController {

    @Resource
    private ProductService productService;

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam Integer quantity) {

        String userId = "testUser"; // 測試階段先固定假帳號

        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String cartKey = "cart:" + userId;
            // 如果商品已存在，則增加數量
            jedis.hincrBy(cartKey, productId.toString(), quantity);
        }

        return "redirect:/cart"; // 重定向回購物車頁面
    }


    @GetMapping("/cart")
    public String viewCart(Model model) {
        String userId = "testUser"; // 固定使用這個測試帳號
        String cartKey = "cart:" + userId;

        Map<String, String> redisCart;
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            redisCart = jedis.hgetAll(cartKey);
        }

        List<CartItemView> cartItems = new ArrayList<>();
        int totalAmount = 0;

        for (Map.Entry<String, String> entry : redisCart.entrySet()) {
            Integer productId = Integer.parseInt(entry.getKey());
            Integer quantity = Integer.parseInt(entry.getValue());

            Product product = productService.getProductById(productId);
            if (product != null) {
                int subtotal = product.getProductPrice() * quantity;
                totalAmount += subtotal;
                cartItems.add(new CartItemView(product.getProductId(), product.getProductName(),
                        product.getProductPrice(), quantity, subtotal));
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "front-end/product/cart"; // 導向 cart.html
    }

    // 更新商品數量
    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam Integer productId,
                                 @RequestParam Integer quantity) {
        String userId = "testUser"; // 假設暫時使用測試用戶

        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String cartKey = "cart:" + userId;
            if (quantity <= 0) {
                jedis.hdel(cartKey, productId.toString()); // 數量 <= 0 時，刪除該商品
            } else {
                jedis.hset(cartKey, productId.toString(), quantity.toString()); // 更新數量
            }
        }

        return "redirect:/cart"; // 重定向回購物車頁面
    }

    // 刪除商品
    @PostMapping("/removeFromCart")
    public String removeFromCart(@RequestParam Integer productId) {
        String userId = "testUser"; // 假設暫時使用測試用戶

        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String cartKey = "cart:" + userId;
            jedis.hdel(cartKey, productId.toString()); // 刪除商品
        }

        return "redirect:/cart"; // 重定向回購物車頁面
    }

    // 顯示用物件
    public static class CartItemView {
        private Integer productId;
        private String productName;
        private int productPrice;
        private int quantity;
        private int subtotal;

        public CartItemView(Integer productId, String productName, int productPrice, int quantity, int subtotal) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }

        public Integer getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getProductPrice() {
            return productPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getSubtotal() {
            return subtotal;
        }
    }
}
