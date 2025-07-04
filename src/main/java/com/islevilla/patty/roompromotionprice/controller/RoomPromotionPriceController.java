package com.islevilla.patty.roompromotionprice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPriceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/backend/roompromotionprice")
@RequiredArgsConstructor
public class RoomPromotionPriceController {

    private final RoomPromotionPriceService roomPromotionPriceSvc;

    @GetMapping("/add")
    public String addRoomPromotionPrice(ModelMap model) {
        model.addAttribute("roomPromotionPrice", new RoomPromotionPrice());
        return "back-end/roompromotionprice/addRoomPromotionPrice";
    }

    @PostMapping("/insert")
    public String insert(@Valid @ModelAttribute RoomPromotionPrice roomPromotionPrice, BindingResult result, ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "back-end/roompromotionprice/addRoomPromotionPrice";
        }
        roomPromotionPriceSvc.addRoomPromotionPrice(roomPromotionPrice);
        return "redirect:/backend/roompromotionprice/listAll";
    }

    @PostMapping("/getOneForUpdate")
    public String getOneForUpdate(@RequestParam("roomPromotionId") Integer id, ModelMap model) {
        model.addAttribute("roomPromotionPrice", roomPromotionPriceSvc.getOneRoomPromotionPrice(id));
        return "back-end/roompromotionprice/update_promotion_input";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute RoomPromotionPrice roomPromotionPrice, BindingResult result, ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "back-end/roompromotionprice/update_roompromotionprice_input";
        }
        roomPromotionPriceSvc.updateRoomPromotionPrice(roomPromotionPrice);
        return "redirect:/backend/roompromotionprice/listAll";
    }

    @GetMapping("/listAll")
    public String listAllRoomPromotionPrices(ModelMap model) {
        List<RoomPromotionPrice> list = roomPromotionPriceSvc.getAll();
        model.addAttribute("roomPromotionPriceListData", list);
        return "back-end/roompromotionprice/listAllRoomPromotionPrice";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("roomPromotionId") Integer id, ModelMap model) {
        roomPromotionPriceSvc.deleteRoomPromotionPrice(id);
        return "redirect:/backend/roompromotionprice/listAll";
    }
}
