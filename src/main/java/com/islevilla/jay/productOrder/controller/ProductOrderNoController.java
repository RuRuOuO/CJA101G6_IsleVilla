package com.islevilla.jay.productOrder.controller;

import java.util.List;
import java.util.Set;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.islevilla.jay.productOrder.model.ProductOrderService;
import com.islevilla.jay.productOrder.model.ProductOrder;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailService;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetail;

@Controller
@Validated
@RequestMapping("/product-order")
public class ProductOrderNoController {
    
    @Autowired
    ProductOrderService productOrderSvc;
    
    @Autowired
    MembersService memSvc;
    
    @Autowired
    ProductOrderDetailService orderDetailSvc;
    
    //會員中心顯示用
    @PostMapping("memOneOrder")
    public String getmemOneOrder(
            @Valid
            /***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
            @RequestParam("productOrderId") String productOrderId,
            ModelMap model) {
            
        /***************************2.開始查詢資料*********************************************/
        ProductOrder productOrder = productOrderSvc.getOneProductOrder(Integer.valueOf(productOrderId));
        List<ProductOrder> list = productOrderSvc.getAll();
        model.addAttribute("productOrderListData", list); 
        
        List<Members> list3 = memSvc.getAll();
        model.addAttribute("memListData", list3);
        
        List<ProductOrderDetail> orderDetailData = orderDetailSvc.findByProductOrderId(Integer.valueOf(productOrderId));
        model.addAttribute("orderDetailData", orderDetailData);
        
        if (productOrder == null) {
            model.addAttribute("errorMessage", "查無資料");
            return "back-end/product-order/select_page";
        }
        /***************************3.查詢完成,準備轉交(Send the Success view)*****************/
        model.addAttribute("productOrder", productOrder);
        model.addAttribute("memOneOrder", "true"); 
        return "front-end/product-order/memOneOrder"; 
    }
    
    @PostMapping("getOne_For_Display")
    public String getOne_For_Display(
            @Valid
            /***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
            @NotEmpty(message="請輸入訂單編號")
            @Digits(fraction = 0, message = "訂單編號有誤", integer = 2)
            @Min(value=1, message = "訂單編號不能小於{value}")
            @RequestParam("productOrderId") String productOrderId,
            ModelMap model) {
            
        /***************************2.開始查詢資料*********************************************/
        ProductOrder productOrder = productOrderSvc.getOneProductOrder(Integer.valueOf(productOrderId));
        
        List<ProductOrder> list = productOrderSvc.getAll();
        model.addAttribute("productOrderListData", list); 
        
        List<Members> list3 = memSvc.getAll();
        model.addAttribute("memListData", list3);
        
        if (productOrder == null) {
            model.addAttribute("errorMessage", "查無資料");
            return "back-end/product-order/select_page";
        }
        /***************************3.查詢完成,準備轉交(Send the Success view)*****************/
        model.addAttribute("productOrder", productOrder);
        model.addAttribute("getOne_For_Display", "true"); 
        return "back-end/product-order/listOneProductOrder"; 
    }
    
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ModelAndView handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations ) {
            strBuilder.append(violation.getMessage() + "<br>");
        }
        //==== 以下是當前面返回 /src/main/resources/templates/back-end/product-order/select_page.html用的 ====   

        List<ProductOrder> list = productOrderSvc.getAll();
        model.addAttribute("productOrderListData", list);    
        
        model.addAttribute("memberVO", new Members());
        List<Members> list3 = memSvc.getAll();
        model.addAttribute("memListData", list3);
        String message = strBuilder.toString();
        return new ModelAndView("back-end/product-order/select_page", "errorMessage", "請修正以下錯誤:<br>"+message);
    }

    // 顯示指定會員的所有歷史訂單
    @GetMapping("/member/memberOrderList")
    public String showMemberHistoryOrders(@RequestParam("memberId") Integer memberId, Model model) {
        List<ProductOrder> orderList = productOrderSvc.getMemAllOrder(memberId);
        model.addAttribute("orderList", orderList);
        return "front-end/product-order/memberOrderList";
    }
    
    // 顯示會員訂單
    @GetMapping("memOrders")
    public String listMemAllOrder(HttpSession session, Model model) {
        Members loggedInMember = (Members) session.getAttribute("member");
        
        if (loggedInMember == null) {
            return "redirect:/member/login";
        }
        
        Integer loginMemNo = loggedInMember.getMemberId();
        
        List<ProductOrder> memOrderList = productOrderSvc.getMemAllOrder(loginMemNo);
        model.addAttribute("loginMemNo", loginMemNo);
        model.addAttribute("memOrderList", memOrderList);
        return "front-end/product-order/memberOrderList";
    }

    // 會員訂單詳情頁面
    @GetMapping("memberOrderDetail")
    public String memberOrderDetail(@RequestParam("orderNo") Integer orderNo, HttpSession session, Model model) {
        // 檢查會員是否已登入
        Members loggedInMember = (Members) session.getAttribute("member");
        if (loggedInMember == null) {
            return "redirect:/member/login";
        }
        
        // 查詢訂單
        ProductOrder order = productOrderSvc.getOneProductOrder(orderNo);
        if (order == null) {
            return "redirect:/product-order/memOrders";
        }
        
        // 檢查訂單是否屬於當前登入會員
        if (!order.getMember().getMemberId().equals(loggedInMember.getMemberId())) {
            return "redirect:/product-order/memOrders";
        }
        
        // 查詢訂單明細
        List<ProductOrderDetail> orderDetails = orderDetailSvc.findByProductOrderId(orderNo);
        
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "front-end/product-order/memberOrderDetail";
    }

    @PostMapping("/confirmOrder")
    public String confirmOrder(@RequestParam("orderId") Integer orderId, HttpSession session) {
        Members loggedInMember = (Members) session.getAttribute("member");
        if (loggedInMember == null) {
            return "redirect:/member/login";
        }
        ProductOrder order = productOrderSvc.getOneProductOrder(orderId);
        // 防呆：只能改自己的訂單
        if (order != null && order.getMember().getMemberId().equals(loggedInMember.getMemberId()) && order.getOrderStatus() == 1) {
            order.setOrderStatus((byte)2); // 2=已完成
            productOrderSvc.updateProductOrder(order);
        }
        return "redirect:/product-order/memOrders";
    }
    
} 