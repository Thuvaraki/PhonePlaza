package com.example.PhonePlaza.Controller;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.LoginRequestDTO;
import com.example.PhonePlaza.DTO.SignUpRequestDTO;
import com.example.PhonePlaza.Service.UserService;
import com.example.PhonePlaza.Util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/phoneplaza/auth")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping(path = "/signup")
    public ResponseEntity<APIResponse> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO){
        APIResponse apiResponse = userService.signUp(signUpRequestDTO);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
    @PostMapping("/emailVerificationUsingOTP/{email}/{otp}")
    public ResponseEntity<APIResponse> EmailVerificationUsingOTP(@PathVariable String email, @PathVariable String otp){
        APIResponse apiResponse=userService.EmailVerificationUsingOTP(email,otp);

        return ResponseEntity
                .status(apiResponse.getStatus())
                .body(apiResponse);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<APIResponse> Login(@RequestBody LoginRequestDTO LoginRequestDTO){
        APIResponse apiResponse = userService.Login(LoginRequestDTO);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

}
