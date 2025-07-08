package com.islevilla.common;

import com.islevilla.jay.productOrder.model.ProductOrder;
import com.islevilla.jay.productOrder.model.ProductOrderService;
import com.islevilla.lai.members.model.Members;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import java.util.List;

@ControllerAdvice
public class GlobalMemberModelAttribute {
    @Autowired
    private ProductOrderService productOrderSvc;

    @ModelAttribute
    public void addMemberToModel(HttpSession session, Model model) {
        Members member = (Members) session.getAttribute("member");
        if (member != null) {
            model.addAttribute("member", member);
        }
    }

    @ModelAttribute("pendingOrderCount")
    public Long getPendingOrderCount(HttpSession session) {
        Members loggedInMember = (Members) session.getAttribute("member");
        if (loggedInMember == null) {
            return 0L;
        }
        List<ProductOrder> memOrderList = productOrderSvc.getMemAllOrder(loggedInMember.getMemberId());
        return memOrderList.stream()
            .filter(order -> order.getOrderStatus() != null && order.getOrderStatus() == 1)
            .count();
    }
} 