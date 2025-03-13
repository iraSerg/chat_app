package com.irina.chat_app.controller;


import com.irina.chat_app.dto.ResetPasswordDto;
import com.irina.chat_app.entity.User;
import com.irina.chat_app.exception.UserNotFoundException;
import com.irina.chat_app.service.UserService;
import com.irina.chat_app.utils.Utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Optional;

import static com.irina.chat_app.utils.TokenGenerator.generateToken;


@Validated
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordController {

    private final JavaMailSender mailSender;
    private final UserService userService;
    private  final MessageSource messageSource;
    @GetMapping("/forgot_password")
    public String getForgotPasswordPage() {

        return "forgotPasswordPage";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model,Locale locale) {
        String email = request.getParameter("email");
        String token = generateToken(30);
        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink,locale);
            model.addAttribute("message",messageSource.getMessage("reset.password.link.sent", null, locale) );
            log.info("Reset password link sent successfully to: {}", email);

        }  catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Error sending email to {}: {}", email, e.getMessage());
            model.addAttribute("error",messageSource.getMessage("reset.password.message.send.error", null, locale) );

        } catch (UserNotFoundException e) {
            log.error("User  not found for email: {}", email);
            model.addAttribute("error",  messageSource.getMessage("password.recovery.user.not.found", null, locale));

        }
        catch (Exception ex) {
            model.addAttribute("error",ex.getMessage() );
        }

        return "forgotPasswordPage";
    }
    public void sendEmail(String recipientEmail, String link,Locale locale)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("chat-app@chat.com", "Chat-app Support");
        helper.setTo(recipientEmail);

        String subject = messageSource.getMessage("reset.password.subject", null, locale);
        String content = "<p>" + messageSource.getMessage("reset.password.message", null, locale) + "</p>"
                + "<p><a href=\"" + link + "\">" + messageSource.getMessage("reset.password.link", null, locale) + "</a></p>"
                + "<br>"
                + "<p>" + messageSource.getMessage("reset.password.ignore", null, locale) + "</p>";
        helper.setSubject(subject);

        helper.setText(content, true);
        mailSender.send(message);
        log.info("Reset password email sent to: {}", recipientEmail);
    }


    @GetMapping("/reset_password")
    public String showResetPasswordPage(@Param(value = "token") String token, Model model,Locale locale) {
        model.addAttribute("token", token);
        Optional<User> user = userService.getByResetPasswordToken(token);
        if(user.isEmpty()) {
            log.warn("Invalid reset password token: {}", token);
            model.addAttribute("error", messageSource.getMessage("reset.password.token.invalid", null, locale));
            return "forgotPasswordPage";
        }
        return "resetPasswordPage";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(@ModelAttribute @Validated ResetPasswordDto resetPasswordDto,
                                       BindingResult bindingResult, Model model,RedirectAttributes redirectAttributes,Locale locale) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in reset password form with token: {}: {}", resetPasswordDto.getToken(), bindingResult.getAllErrors());
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "resetPasswordPage";
        }

        Optional<User> user = userService.getByResetPasswordToken(resetPasswordDto.getToken());

        if (user.isPresent()) {
            userService.updatePassword(user.get(), resetPasswordDto.getPassword());
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("password.updated.successfully", null, locale));
            log.info("Password reset successfully for token: {}", resetPasswordDto.getToken());
            return "redirect:/login";
        } else {
            log.warn("Invalid token used for password reset: {}", resetPasswordDto.getToken());
            model.addAttribute("message", messageSource.getMessage("failed.to.change.password.invalid.token", null, locale));
            return "resetPasswordPage";
        }
    }

}