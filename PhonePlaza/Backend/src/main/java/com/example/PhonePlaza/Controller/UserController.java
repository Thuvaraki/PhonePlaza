package com.example.PhonePlaza.Controller;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.EditProfileDTO;
import com.example.PhonePlaza.DTO.LoginRequestDTO;
import com.example.PhonePlaza.DTO.SignUpRequestDTO;
import com.example.PhonePlaza.Service.UserService;
import com.example.PhonePlaza.Util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<APIResponse> ForgotPassword(@RequestParam String email){
        APIResponse apiResponse = userService.ForgotPassword(email);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<APIResponse> ChangePassword(@RequestParam String email,@RequestParam String password){
        System.out.println("email: "+email);
        System.out.println("password: "+password);
        APIResponse apiResponse = userService.ChangePassword(email,password  );

        return ResponseEntity
                .status(apiResponse.getStatus())
                .body(apiResponse);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<APIResponse> logout() {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatus(HttpStatus.OK.value());
        apiResponse.setData("Logged out successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(path = "/viewProfile")
    public ResponseEntity<APIResponse> viewProfile(@RequestParam String email){
        APIResponse apiResponse = userService.viewProfile(email);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PutMapping(path = "/editProfile")
    public ResponseEntity<APIResponse> editProfile(@RequestParam String email,@RequestBody EditProfileDTO editProfileDTO){
        APIResponse apiResponse = userService.editProfile(email,editProfileDTO);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }


}
