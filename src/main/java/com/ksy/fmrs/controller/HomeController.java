package com.ksy.fmrs.controller;

import com.ksy.fmrs.service.PlayerService;
import com.ksy.fmrs.service.InitializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private final PlayerService playerService;
    private final InitializationService initializationService;

//    @GetMapping("/home")
//    public String home(Model model) {
////
////        model.addAttribute("featuredPlayers", searchPlayerResponseDto);
//        return "home";
//    }

}
