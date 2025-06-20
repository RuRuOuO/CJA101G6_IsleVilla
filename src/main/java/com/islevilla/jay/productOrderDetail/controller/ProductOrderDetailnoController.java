package com.islevilla.jay.productOrderDetail.controller;

import com.islevilla.jay.productOrderDetail.model.ProductOrderDetail;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailId;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;

@Controller
@Validated
@RequestMapping("/product-order-detail")
public class ProductOrderDetailnoController {

    @Autowired
    ProductOrderDetailService orderDetailSvc;

    public ProductOrderDetailnoController(ProductOrderDetailService orderDetailSvc) {
        this.orderDetailSvc = orderDetailSvc;
    }

    @PostMapping("getOne_For_Display")
    public String getOne_For_Display(
            @NotEmpty(message = "請輸入訂單編號")
            @RequestParam("productOrderId") String productOrderId,
            @NotEmpty(message = "請輸入商品編號")
            @RequestParam("productId") String productId,
            ModelMap model) {

        ProductOrderDetailId id = new ProductOrderDetailId(
            Integer.valueOf(productOrderId),
            Integer.valueOf(productId)
        );
        ProductOrderDetail orderDetail = orderDetailSvc.getOneOrderDetail(id);

        List<ProductOrderDetail> list = orderDetailSvc.getAll();
        model.addAttribute("orderDetailListData", list);

        model.addAttribute("orderDetail", orderDetail);
        model.addAttribute("getOne_For_Display", "true");
        return "back-end/product-order/listOneOrderDetail";
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ModelAndView handleError(HttpServletRequest req,
                                  ConstraintViolationException e,
                                  Model model) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            strBuilder.append(violation.getMessage()).append("<br>");
        }

        List<ProductOrderDetail> list = orderDetailSvc.getAll();
        model.addAttribute("orderDetailListData", list);     // for select_page.html 第97 109行用
        String message = strBuilder.toString();
        return new ModelAndView("back-end/product-order/select_page",
                              "errorMessage",
                              "請修正以下錯誤:<br>" + message);
    }
} 