package com.islevilla.yin.cart.controller;
import com.islevilla.yin.cart.model.CartDTO;
import com.islevilla.yin.cart.model.CartService;
import com.islevilla.lai.members.model.Members;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/addToCart")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam Integer productId,
                                       @RequestParam Integer quantity,
                                       HttpSession session) {
        // 檢查會員是否已登入
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(401).body("請先登入會員");
        }
        
        String userId = String.valueOf(member.getMemberId()); // 使用實際會員ID
        cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok().body("加入購物車成功");
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        // 檢查會員是否已登入
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login?redirect=/cart";
        }
        
        String userId = String.valueOf(member.getMemberId()); // 使用實際會員ID
        List<CartDTO> cartItems = cartService.getCartItems(userId);
        int totalAmount = cartItems.stream().mapToInt(CartDTO::getSubtotal).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "front-end/product/cart"; // 導向 cart.html
    }

    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam Integer productId,
                                 @RequestParam Integer quantity,
                                 HttpSession session) {
        // 檢查會員是否已登入
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login?redirect=/cart";
        }
        
        String userId = String.valueOf(member.getMemberId()); // 使用實際會員ID
        cartService.updateQuantity(userId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/removeFromCart")
    public String removeFromCart(@RequestParam Integer productId,
                                 HttpSession session) {
        // 檢查會員是否已登入
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login?redirect=/cart";
        }
        
        String userId = String.valueOf(member.getMemberId()); // 使用實際會員ID
        cartService.removeFromCart(userId, productId);
        return "redirect:/cart";
    }

    @PostMapping("/clearCart")
    public String clearCart(HttpSession session) {
        // 檢查會員是否已登入
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login?redirect=/cart";
        }
        
        String userId = String.valueOf(member.getMemberId()); // 使用實際會員ID
        cartService.clearCart(userId);
        return "redirect:/cart";
    }
}
