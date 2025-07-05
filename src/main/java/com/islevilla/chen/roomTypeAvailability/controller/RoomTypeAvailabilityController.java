package com.islevilla.chen.roomTypeAvailability.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailability;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityId;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.chen.util.map.RoomTypeName;

@Controller
@RequestMapping("/backend/roomTypeAvailability")
public class RoomTypeAvailabilityController {

    @Autowired
    private RoomTypeAvailabilityService roomTypeAvailabilityService;
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private RoomTypeName roomTypeName;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
}
