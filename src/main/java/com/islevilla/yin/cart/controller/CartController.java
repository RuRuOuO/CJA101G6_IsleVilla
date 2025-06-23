package com.islevilla.yin.cart.controller;
import com.islevilla.yin.cart.model.CartDTO;
import com.islevilla.yin.cart.model.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam Integer quantity) {
        String userId = "testUser"; // 測試階段先固定假帳號
        cartService.addToCart(userId, productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        String userId = "testUser"; // 固定使用這個測試帳號
        List<CartDTO> cartItems = cartService.getCartItems(userId);
        int totalAmount = cartItems.stream().mapToInt(CartDTO::getSubtotal).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "front-end/product/cart"; // 導向 cart.html
    }

    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam Integer productId,
                                 @RequestParam Integer quantity) {
        String userId = "testUser"; // 假設暫時使用測試用戶
        cartService.updateQuantity(userId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/removeFromCart")
    public String removeFromCart(@RequestParam Integer productId) {
        String userId = "testUser"; // 假設暫時使用測試用戶
        cartService.removeFromCart(userId, productId);
        return "redirect:/cart";
    }
}
