package com.islevilla.jay.productOrderDetail.controller;

import com.islevilla.jay.productOrder.model.ProductOrder;
import com.islevilla.jay.productOrder.model.ProductOrderService;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetail;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailId;
import com.islevilla.jay.productOrderDetail.model.ProductOrderDetailService;
import com.islevilla.yin.product.model.Product;
import com.islevilla.yin.product.model.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/product-order-detail")
public class ProductOrderDetailController {

    @Autowired
    ProductOrderDetailService orderDetailSvc;

    @Autowired
    ProductService productSvc;

    @Autowired
    ProductOrderService orderSvc;

    // 新增訂單明細
    @GetMapping("addOrderDetail")
    public String addOrderDetail(ModelMap model) {
        ProductOrderDetail orderDetail = new ProductOrderDetail();
        model.addAttribute("orderDetail", orderDetail);
        return "back-end/product-order/addOrderDetail";
    }

    // 在 addOrderDetail 頁面執行 insert
    @PostMapping("insert")
    public String insert(@Valid @ModelAttribute ProductOrderDetail orderDetail,
                        BindingResult result,
                        ModelMap model) throws IOException {
        /*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
        if (result.hasErrors()) {
            return "back-end/product-order/addOrderDetail";
        }

        /*************************** 2.開始新增資料 *****************************************/
        try {
            // 驗證訂單和商品是否存在
            ProductOrder order = orderSvc.getOneProductOrder(orderDetail.getId().getProductOrderId());
            Product product = productSvc.getProductById(orderDetail.getId().getProductId());

            if (order == null) {
                throw new IllegalArgumentException("無法找到訂單編號: " + orderDetail.getId().getProductOrderId());
            }
            if (product == null) {
                throw new IllegalArgumentException("無法找到商品編號: " + orderDetail.getId().getProductId());
            }

            // 設定關聯
            orderDetail.setProductOrder(order);
            orderDetail.setProduct(product);

            // 儲存訂單明細
            orderDetailSvc.addOrderDetail(orderDetail);

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "back-end/product-order/addOrderDetail";
        }

        /*************************** 3.新增完成,準備轉交(Send the Success view) **************/
        List<ProductOrderDetail> list = orderDetailSvc.getAll();
        model.addAttribute("orderDetailListData", list);
        return "back-end/product-order/listAllOrderDetail";
    }

    // 修改訂單明細
    @PostMapping("getOne_For_Update")
    public String getOne_For_Update(@RequestParam("productOrderId") String productOrderId,
                                  @RequestParam("productId") String productId,
                                  ModelMap model) {
        ProductOrderDetailId id = new ProductOrderDetailId(
            Integer.valueOf(productOrderId),
            Integer.valueOf(productId)
        );
        ProductOrderDetail orderDetail = orderDetailSvc.getOneOrderDetail(id);
        model.addAttribute("orderDetail", orderDetail);
        return "back-end/product-order/updateOrderDetail";
    }

    @PostMapping("update")
    public String update(@Valid ProductOrderDetail orderDetail,
                        BindingResult result,
                        ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "back-end/product-order/updateOrderDetail";
        }

        orderDetailSvc.updateOrderDetail(orderDetail);
        List<ProductOrderDetail> list = orderDetailSvc.getAll();
        model.addAttribute("orderDetailListData", list);
        return "back-end/product-order/listAllOrderDetail";
    }

    // 刪除訂單明細
    @PostMapping("delete")
    public String delete(@RequestParam("productOrderId") String productOrderId,
                        @RequestParam("productId") String productId,
                        ModelMap model) {
        ProductOrderDetailId id = new ProductOrderDetailId(
            Integer.valueOf(productOrderId),
            Integer.valueOf(productId)
        );
        orderDetailSvc.deleteOrderDetail(id);
        List<ProductOrderDetail> list = orderDetailSvc.getAll();
        model.addAttribute("orderDetailListData", list);
        return "back-end/product-order/listAllOrderDetail";
    }

    // 顯示所有訂單明細
    @GetMapping("listAllOrderDetail")
    public String listAllOrderDetail(Model model) {
        return "back-end/product-order/listAllOrderDetail";
    }

    // 顯示特定訂單的明細
    @GetMapping("orderDetailByOrderId")
    public String listOrderDetailByOrderId(@RequestParam("orderId") Integer orderId,
                                         Model model) {
        List<ProductOrderDetail> details = orderDetailSvc.findByProductOrderId(orderId);
        model.addAttribute("orderDetails", details);
        return "front-end/product-order/memOrderDetail";
    }

    // 異常處理
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
        model.addAttribute("orderDetailListData", list);
        String message = strBuilder.toString();
        return new ModelAndView("back-end/product-order/select_page",
                              "errorMessage",
                              "請修正以下錯誤:<br>" + message);
    }

    // 提供下拉選單資料
    @ModelAttribute("productListData")
    protected List<Product> referenceProductListData() {
        return productSvc.getAllProducts();
    }

    @ModelAttribute("orderListData")
    protected List<ProductOrder> referenceOrderListData() {
        return orderSvc.getAll();
    }

    @ModelAttribute("orderDetailListData")
    protected List<ProductOrderDetail> orderDetailListData() {
        return orderDetailSvc.getAll();
    }
} 