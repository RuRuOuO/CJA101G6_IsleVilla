package com.islevilla.yin.cart.controller;
import com.islevilla.yin.cart.model.CartDTO;
import com.islevilla.yin.cart.model.CartService;
import com.islevilla.lai.members.model.Members;
import com.islevilla.yin.product.model.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

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
        // 查詢商品庫存
        Integer stock = productService.getProductById(productId).getProductQuantity();
        if (quantity > stock) {
            return ResponseEntity.badRequest().body("加入數量不能大於庫存量");
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
        // 查詢商品庫存
        Integer stock = productService.getProductById(productId).getProductQuantity();
        if (quantity > stock) {
            // 數量超過庫存，導回購物車並顯示錯誤
            return "redirect:/cart?error=overstock";
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

    // 取得購物車內某商品數量
    @GetMapping("/cart/quantity/{productId}")
    @ResponseBody
    public int getCartProductQuantity(@PathVariable Integer productId, HttpSession session) {
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return 0;
        }
        String userId = String.valueOf(member.getMemberId());
        return cartService.getProductQuantityInCart(userId, productId);
    }

    // 結帳前庫存檢查
    @PostMapping("/cart/checkout")
    public String preCheckout(HttpSession session, Model model) {
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login?redirect=/cart";
        }
        String userId = String.valueOf(member.getMemberId());
        List<CartDTO> cartItems = cartService.getCartItems(userId);

        List<String> errorList = new ArrayList<>();
        for (CartDTO item : cartItems) {
            Integer stock = productService.getProductById(item.getProductId()).getProductQuantity();
            if (item.getQuantity() > stock) {
                errorList.add(item.getProductName() + "庫存不足，請重新調整數量（剩餘" + stock + "件）");
            }
        }
        if (!errorList.isEmpty()) {
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", cartItems.stream().mapToInt(CartDTO::getSubtotal).sum());
            model.addAttribute("error", String.join("<br>", errorList));
            return "front-end/product/cart";
        }
        return "redirect:/checkout";
    }
}
