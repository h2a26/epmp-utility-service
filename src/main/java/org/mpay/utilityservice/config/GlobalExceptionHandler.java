package org.mpay.utilityservice.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(Model model) {
        model.addAttribute("success", false);
        model.addAttribute("error", "File is too large! Maximum allowed size is 50MB.");
        return "fragments/upload-result :: error";
    }
}
