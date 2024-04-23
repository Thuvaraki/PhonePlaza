package com.example.PhonePlaza.Controller;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.SignUpRequestDTO;
import com.example.PhonePlaza.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/phoneplaza/auth")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(path = "/signup")
    public ResponseEntity<APIResponse> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO){
        APIResponse apiResponse = userService.signUp(signUpRequestDTO);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PostMapping("/verifyOtp/{email}/{otp}")
    public ResponseEntity<APIResponse> verifyOTP(@PathVariable String email, @PathVariable String otp){
        APIResponse apiResponse=userService.verifyOTP(email,otp);

        return ResponseEntity
                .status(apiResponse.getStatus())
                .body(apiResponse);
    }

}
