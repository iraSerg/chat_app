package com.irina.chat_app.controller;


import com.irina.chat_app.dto.LoginDto;
import com.irina.chat_app.dto.UserCreateDto;
import com.irina.chat_app.dto.UserEditDto;
import com.irina.chat_app.dto.UserReadDto;
import com.irina.chat_app.service.UserService;
import com.irina.chat_app.validator.UserRegistrationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;


@Controller
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserRegistrationValidator validator;
    private final MessageSource messageSource;

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {

        model.addAttribute("user", new UserCreateDto(null, null, null, null));
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(
            @Validated @ModelAttribute("user") UserCreateDto user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        validator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in registration: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());

            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/registration";
        }

        try {
            UserReadDto userReadDto = userService.create(user);
            log.info("User registered successfully: {}", userReadDto.getUsername());
            String successMessage = messageSource.getMessage("registration.success.message", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/login";

        } catch (Exception e) {
            log.error("Error during user registration", e);
            String errorMessage = messageSource.getMessage("registration.failure.message", new Object[]{e.getMessage()}, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/registration";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:/chat";
    }

    @PostMapping("/login")
    public String authenticateUser(@ModelAttribute @Validated LoginDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        return "redirect:/account";
    }

    @GetMapping("/account")
    public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        UserReadDto user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "account";
    }

    @PostMapping("/account/update")
    public String updateUserAccount(@AuthenticationPrincipal UserDetails userDetails, Model model, @ModelAttribute @Validated UserEditDto user, BindingResult bindingResult, Locale locale) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in account update: {}", bindingResult.getAllErrors());
            model.addAttribute("user", user);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "account";
        }

        UserReadDto userReadDto = userService.update(userDetails.getUsername(), user);
        model.addAttribute("user", userReadDto);
        model.addAttribute("successMessage", messageSource.getMessage("account.update.success", null, locale));
        return "account";
    }

}