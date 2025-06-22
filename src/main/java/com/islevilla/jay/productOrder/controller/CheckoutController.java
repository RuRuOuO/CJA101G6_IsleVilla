package com.islevilla.jay.productOrder.controller;

import com.islevilla.yin.cart.model.CartDTO;
import com.islevilla.yin.cart.model.CartService;
import com.islevilla.jay.productOrder.model.ProductOrder;
import com.islevilla.jay.productOrder.model.ProductOrderService;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetail;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailId;
import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailService;
import com.islevilla.jay.memberCoupon.model.MemberCoupon;
import com.islevilla.jay.memberCoupon.model.MemberCouponService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MembersService membersService;

    @Autowired
    private ProductOrderDetailService productOrderDetailService;

    @Autowired
    private MemberCouponService memberCouponService;

    @GetMapping("/checkout")
    public String checkout(@RequestParam(value = "redirect", required = false) String redirect, 
                          Model model, HttpSession session) {
        // 檢查會員是否已登入
        Members loggedInMember = (Members) session.getAttribute("member");
        if (loggedInMember == null) {
            // 如果未登入，重定向到登入頁面並帶上redirect參數
            if (redirect != null && !redirect.isEmpty()) {
                return "redirect:/member/login?redirect=" + redirect;
            } else {
                return "redirect:/member/login";
            }
        }
        
        String userId = "testUser"; // 固定使用這個測試帳號
        List<CartDTO> cartItems = cartService.getCartItems(userId);
        
        // 檢查購物車是否為空
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        int totalAmount = cartItems.stream().mapToInt(CartDTO::getSubtotal).sum();

        // 查詢會員可用的優惠券
        List<MemberCoupon> availableCoupons = memberCouponService.findValidCouponsByMemberId(loggedInMember.getMemberId(), totalAmount);

        // 使用實際登入的會員資訊
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("member", loggedInMember);
        model.addAttribute("availableCoupons", availableCoupons);
        return "front-end/product/checkout"; // 導向 checkout.html
    }

    @PostMapping("/checkout")
    @Transactional
    public String processCheckout(@RequestParam String contactName,
                                  @RequestParam String contactPhone,
                                  @RequestParam(required = false) String contactAddress,
                                  @RequestParam(required = false) String orderNote,
                                  @RequestParam Integer shippingMethod,
                                  @RequestParam Integer paymentMethod,
                                  @RequestParam(required = false) Integer selectedCouponId,
                                  @RequestParam(required = false) String cardNumber,
                                  @RequestParam(required = false) String cardExpiry,
                                  @RequestParam(required = false) String cardCvv,
                                  @RequestParam(required = false) String cardHolderName,
                                  Model model,
                                  HttpSession session) {
        // 檢查會員是否已登入
        Members loggedInMember = (Members) session.getAttribute("member");
        if (loggedInMember == null) {
            return "redirect:/member/login";
        }
        
        String userId = "testUser"; // 固定使用這個測試帳號
        List<CartDTO> cartItems = cartService.getCartItems(userId);
        
        // 檢查購物車是否為空
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        int totalAmount = cartItems.stream().mapToInt(CartDTO::getSubtotal).sum();
        
        try {
            // 使用實際登入的會員資訊
            Members member = loggedInMember;
            
            // 處理優惠券
            Integer discountAmount = 0;
            if (selectedCouponId != null) {
                // 查詢選中的優惠券
                List<MemberCoupon> availableCoupons = memberCouponService.findValidCouponsByMemberId(member.getMemberId(), totalAmount);
                MemberCoupon selectedCoupon = availableCoupons.stream()
                    .filter(mc -> mc.getCoupon().getCouponId().equals(selectedCouponId))
                    .findFirst()
                    .orElse(null);
                
                if (selectedCoupon != null) {
                    discountAmount = memberCouponService.calculateDiscount(selectedCoupon, totalAmount);
                }
            }
            
            // 計算實付金額
            int finalAmount = totalAmount - discountAmount;
            
            // 如果是飯店自取，使用飯店地址
            String finalAddress = contactAddress;
            if (shippingMethod == 1) { // 飯店自取
                finalAddress = "微嶼飯店櫃檯領取";
            }
            
            // 創建訂單
            ProductOrder order = new ProductOrder();
            order.setMember(member);
            order.setProductOrderAmount(totalAmount);
            order.setDiscountAmount(discountAmount);
            order.setProductPaidAmount(finalAmount);
            order.setPaymentMethod(paymentMethod.byteValue()); // 使用選擇的付款方式
            order.setShippingMethod(shippingMethod.byteValue()); // 使用選擇的配送方式
            order.setContactAddress(finalAddress);
            order.setContactName(contactName);
            order.setContactPhone(contactPhone);
            order.setNote(orderNote);
            order.setOrderStatus((byte) 0); // 訂單成立
            order.setOrderTime(LocalDateTime.now());
            
            // 先保存訂單以獲得ID
            productOrderService.addProductOrder(order);
            
            // 如果有使用優惠券，設置優惠券關聯
            if (selectedCouponId != null) {
                // 這裡需要注入CouponService來獲取優惠券
                // 暫時先不設置，等後面再完善
            }
            
            // 創建訂單明細
            for (CartDTO cartItem : cartItems) {
                Product product = productService.getProductById(cartItem.getProductId());
                if (product != null) {
                    ProductOrderDetail detail = new ProductOrderDetail();
                    ProductOrderDetailId detailId = new ProductOrderDetailId();
                    detailId.setProductOrderId(order.getProductOrderId());
                    detailId.setProductId(cartItem.getProductId());
                    detail.setId(detailId);
                    detail.setProductOrder(order);
                    detail.setProduct(product);
                    detail.setProductOrderQuantity(cartItem.getQuantity());
                    detail.setProductOrderPrice(cartItem.getProductPrice());
                    detail.setProductOrderName(cartItem.getProductName());
                    
                    productOrderDetailService.addOrderDetail(detail);
                }
            }
            
            // 清空購物車
            cartService.clearCart(userId);
            
            // 重定向到訂單成功頁面
            return "redirect:/order-success";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "訂單建立失敗，請稍後再試");
            return "redirect:/checkout";
        }
    }
    
    @GetMapping("/order-success")
    public String orderSuccess() {
        return "front-end/product/order-success";
    }
} 