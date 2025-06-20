package com.islevilla.jay.productOrder.controller;

import com.islevilla.jay.coupon.model.Coupon;
import com.islevilla.jay.coupon.model.CouponService;
import com.islevilla.jay.memberCoupon.model.MemberCouponService;
import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.jay.productOrder.model.ProductOrder;
import com.islevilla.jay.productOrder.model.ProductOrderService;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetail;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailService;
import com.islevilla.yin.cart.model.CartService;
import com.islevilla.yin.cart.model.CartDTO;
import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/product-order")
public class ProductOrderController {

    @Autowired
    ProductOrderService productOrderSvc;

    @Autowired
    MembersService memberSvc;

    @Autowired
    CouponService couponSvc;

    @Autowired
    ProductOrderDetailService orderDetailSvc;

    @Autowired
    CartService cartSvc;

    @Autowired
    ProductService productSvc;

    @Autowired
    MemberCouponService memberCouponSvc;

    /*
     * This method will serve as addProductOrder.html handler.
     */
    @GetMapping("addProductOrder")
    public String addProductOrder(ModelMap model) {
        ProductOrder productOrder = new ProductOrder();
        model.addAttribute("productOrder", productOrder);
        return "back-end/product-order/addProductOrder";
    }

    /*
     * This method will be called on addProductOrder.html form submission, handling POST request
     * It also validates the user input
     */
    @PostMapping("insert")
    public String insert(@Valid 
            @ModelAttribute("productOrder") ProductOrder productOrder,
            BindingResult result,
            HttpSession session,
            @RequestParam("couponId") Integer couponId,
            @RequestParam("shippingMethod") Integer shippingMethod,
            @RequestParam(value = "contactNameHome", required = false) String contactNameHome,
            @RequestParam(value = "contactPhoneHome", required = false) String contactPhoneHome,
            @RequestParam(value = "contactAddressHome", required = false) String contactAddressHome,
            @RequestParam(value = "contactNameHotel", required = false) String contactNameHotel,
            @RequestParam(value = "contactPhoneHotel", required = false) String contactPhoneHotel,
            @RequestParam(value = "contactAddressHotel", required = false) String contactAddressHotel,
            ModelMap model) throws IOException {
        
        /*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
        Members loggedInMember = (Members) session.getAttribute("loggedInMember");
        
        if (loggedInMember == null) {
            return "redirect:/member/loginMem"; 
        }
        
        Integer loginMemNo = loggedInMember.getMemberId();
        String userId = String.valueOf(loginMemNo); // 會員ID作為Redis購物車key
        
        if (productOrder.getOrderTime() == null) {
            productOrder.setOrderTime(LocalDateTime.now());  
        }
    
        if (result.hasErrors()) {
            System.out.println(result);
            Members members = memberSvc.getOneMember(loginMemNo);
            List<CartDTO> cartDetails = cartSvc.getCartItems(userId);
            model.addAttribute("memberVO", members);
            model.addAttribute("cartDetails", cartDetails);
            return "front-end/product-order/checkout";
        }

        /*************************** 2.開始新增資料 *****************************************/
        try {
            Members members = memberSvc.getOneMember(loginMemNo);
            productOrder.setMember(members);
            
            if (couponId != null) {
                Optional<Coupon> couponOpt = couponSvc.findById(couponId);
                if (couponOpt.isPresent()) {
                    Coupon coupon = couponOpt.get();
                    productOrder.setCoupon(coupon);
                    productOrder.setDiscountAmount(coupon.getDiscountValue());
                    
                    // 記錄會員使用優惠券的記錄
                    memberCouponSvc.recordCouponUsage(loginMemNo, couponId);
                }
            }
            
            if (shippingMethod == 0) {
                productOrder.setContactName(contactNameHome);
                productOrder.setContactPhone(contactPhoneHome);
                productOrder.setContactAddress(contactAddressHome);
            } else if (shippingMethod == 1) {
                productOrder.setContactName(contactNameHotel);
                productOrder.setContactPhone(contactPhoneHotel);
                productOrder.setContactAddress(contactAddressHotel);
            }
    
            List<CartDTO> cartItems = cartSvc.getCartItems(userId);
            for (CartDTO cartItem : cartItems) {
                ProductOrderDetail detail = new ProductOrderDetail();
                Product product = productSvc.getProductById(cartItem.getProductId());
                if (product == null) {
                    throw new RuntimeException("無此商品: " + cartItem.getProductId());
                }
                detail.setProduct(product);
                detail.setProductOrderQuantity(cartItem.getQuantity());
                detail.setProductOrderPrice(product.getProductPrice());
                detail.setProductName(product);
                detail.setProductOrder(productOrder);
                productOrderSvc.addOrderDetail(productOrder, detail);
            }
            
            productOrderSvc.addProductOrder(productOrder);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        /*************************** 3.新增完成,準備轉交(Send the Success view) **************/
        model.addAttribute("productOrder", productOrder);
        
        cartSvc.clearCart(userId);  // 新增訂單時同時清空購物車內容
        return "redirect:/product-order/checkoutResult?orderNo=" + productOrder.getProductOrderId();
    }

    /*
     * This method will be called on listAllProductOrder.html form submission, handling POST request
     */
    @PostMapping("getOne_For_Update")
    public String getOne_For_Update(@RequestParam("productOrderId") String productOrderId, ModelMap model) {
        ProductOrder productOrder = productOrderSvc.getOneProductOrder(Integer.valueOf(productOrderId));
        model.addAttribute("productOrder", productOrder);
        return "back-end/product-order/update_productOrder_input";// 查詢完成後轉交update_productOrder_input.html
    }

    /*
     * This method will be called on update_productOrder_input.html form submission, handling POST request
     * It also validates the user input
     */
    @PostMapping("update")
    public String update(@Valid ProductOrder productOrder, BindingResult result, ModelMap model) throws IOException {
        /*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
        if (result.hasErrors()) {
            return "back-end/product-order/update_productOrder_input";
        }
        /*************************** 2.開始修改資料 *****************************************/
        productOrderSvc.updateProductOrder(productOrder);
        
        /*************************** 3.修改完成,準備轉交(Send the Success view) **************/
        productOrder = productOrderSvc.getOneProductOrder(productOrder.getProductOrderId());
        model.addAttribute("productOrder", productOrder);
        model.addAttribute("productOrderDetails", productOrder.getProductOrderDetails());
        return "back-end/product-order/listOneProductOrder";
    }

    /*
     * This method will be called on listAllProductOrder.html form submission, handling POST request
     */
    @PostMapping("delete")
    public String delete(@RequestParam("productOrderId") String productOrderId, ModelMap model) {
        productOrderSvc.deleteProductOrder(Integer.valueOf(productOrderId));
        List<ProductOrder> list = productOrderSvc.getAll();
        model.addAttribute("productOrderListData", list);
        // model.addAttribute("success", "- (刪除成功)");
        return "back-end/product-order/listAllProductOrder";
    }

    // 顯示所有訂單
    @GetMapping("listAllProductOrder")
    public String listAllProductOrder(Model model) {
        return "back-end/product-order/listAllProductOrder";
    }

    // 顯示會員訂單
    @GetMapping("memOrders")
    public String listMemAllOrder(HttpSession session, Model model) {
        Members loggedInMember = (Members) session.getAttribute("loggedInMember");
        
        if (loggedInMember == null) {
            return "redirect:/member/loginMem";
        }
        
        Integer loginMemNo = loggedInMember.getMemberId();
        
        List<ProductOrder> memOrderList = productOrderSvc.getMemAllOrder(loginMemNo);
        model.addAttribute("loginMemNo", loginMemNo);
        model.addAttribute("memOrderList", memOrderList);
        return "front-end/product-order/memOrderList";
    }

    // 複合查詢
    @PostMapping("listProductOrders_ByCompositeQuery")
    public String listAllProductOrder(HttpServletRequest req, Model model) {
        Map<String, String[]> map = req.getParameterMap();
        List<ProductOrder> list = productOrderSvc.getAll(map);
        model.addAttribute("productOrderListData", list);
        return "back-end/product-order/listAllProductOrder";
    }

    // 提供下拉選單資料
    @ModelAttribute("memberListData")
    protected List<Members> referenceMemberListData() {
        return memberSvc.getAll();
    }

    @ModelAttribute("couponListData")
    protected List<Coupon> referenceCouponListData() {
        return couponSvc.getAll();
    }

    @ModelAttribute("paymentMethodMapData")
    protected Map<Byte, String> referencePaymentMethodMapData() {
        Map<Byte, String> map = new LinkedHashMap<>();
        map.put((byte)0, "信用卡");
        map.put((byte)1, "現金付款");
        return map;
    }

    @ModelAttribute("shippingMethodMapData")
    protected Map<Byte, String> referenceShippingMethodMapData() {
        Map<Byte, String> map = new LinkedHashMap<>();
        map.put((byte)0, "宅配");
        map.put((byte)1, "飯店自取");
        return map;
    }

    @ModelAttribute("orderStatusMapData")
    protected Map<Byte, String> referenceOrderStatusMapData() {
        Map<Byte, String> map = new LinkedHashMap<>();
        map.put((byte)0, "成立");
        map.put((byte)1, "配送中");
        map.put((byte)2, "已取貨");
        return map;
    }

    // 去除BindingResult中某個欄位的FieldError紀錄
    public BindingResult removeFieldError(ProductOrder productOrder, BindingResult result, String removedFieldname) {
        List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
                .filter(fieldname -> !fieldname.getField().equals(removedFieldname))
                .collect(Collectors.toList());
        result = new BeanPropertyBindingResult(productOrder, "productOrder");
        for (FieldError fieldError : errorsListToKeep) {
            result.addError(fieldError);
        }
        return result;
    }

    // 訂單結果頁面
    @GetMapping("checkoutResult")
    public String checkoutResult(@RequestParam("orderNo") Integer orderNo, ModelMap model) {
        ProductOrder order = productOrderSvc.getOneProductOrder(orderNo);
        List<ProductOrderDetail> orderDetails = orderDetailSvc.findByProductOrderId(orderNo);
        
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "front-end/product-order/checkoutResult";
    }

    @GetMapping("select_page")
    public String select_page(Model model) {
        return "back-end/product-order/select_page";
    }
}
