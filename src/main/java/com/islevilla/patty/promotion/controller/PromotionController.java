package com.islevilla.patty.promotion.controller;

import com.islevilla.patty.promotion.model.Promotion;
import com.islevilla.patty.promotion.model.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/backend/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionSvc;

    @GetMapping("/add")
    public String addPromotion(ModelMap model) {
        model.addAttribute("promotion", new Promotion());
        return "back-end/promotion/addPromotion";
    }

    @PostMapping("/insert")
    public String insert(@Valid @ModelAttribute Promotion promotion, BindingResult result, ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "back-end/promotion/addPromotion";
        }
        promotionSvc.addPromotion(promotion);
        return "redirect:/backend/promotion/listAll";
    }

    @PostMapping("/getOneForUpdate")
    public String getOneForUpdate(@RequestParam("roomPromotionId") Integer id, ModelMap model) {
        model.addAttribute("promotion", promotionSvc.getOnePromotion(id));
        return "back-end/promotion/update_promotion_input";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute Promotion promotion, BindingResult result, ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "back-end/promotion/update_promotion_input";
        }
        promotionSvc.updatePromotion(promotion);
        return "redirect:/backend/promotion/listAll";
    }

    @GetMapping("/listAll")
    public String listAllPromotions(ModelMap model) {
        List<Promotion> list = promotionSvc.getAll();
        model.addAttribute("promotionListData", list);
        return "back-end/promotion/listAllPromotion";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("roomPromotionId") Integer id, ModelMap model) {
        promotionSvc.deletePromotion(id);
        return "redirect:/backend/promotion/listAll";
    }
    
    @GetMapping("/search")
    public String searchPromotion(@RequestParam("id") Integer roomPromotionId, Model model) {
        Promotion promotion = promotionSvc.getOnePromotion(roomPromotionId);
        model.addAttribute("searchResult", promotion);
        return "back-end/promotion/lisstAllpromotion"; 
    }
}