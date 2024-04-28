package com.example.PhonePlaza.Service;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.EditProfileDTO;
import com.example.PhonePlaza.DTO.LoginRequestDTO;
import com.example.PhonePlaza.DTO.SignUpRequestDTO;
import com.example.PhonePlaza.DTO.ViewProfileResponseDTO;
import com.example.PhonePlaza.Entity.User;
import com.example.PhonePlaza.ExceptionAndHandler.UserNotFoundException;
import com.example.PhonePlaza.Repository.UserRepository;
import com.example.PhonePlaza.Util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    private static final String ALLOWED_CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 4;
    private static final int OTP_EXPIRY_DURATION_MINUTES = 5;

    @Transactional
    public APIResponse signUp(SignUpRequestDTO signUpRequestDTO) {
        APIResponse apiResponse = new APIResponse();

//  VALIDATE
        User isUser = userRepository.findByEmail(signUpRequestDTO.getEmail());

        //   CREATING ENTITY
        if (isUser == null || !isUser.isVerified()) {
            User user = new User();
            user.setUserName(signUpRequestDTO.getUserName());
            user.setEmail(signUpRequestDTO.getEmail());
            user.setPassword(signUpRequestDTO.getPassword());

            // OTP GENERATION
            String otp = generateOTP();
            user.setVerificationCode(otp);

            // Calculate OTP expiration time
            Date expiryTime = calculateExpiryTime();
            user.setVerificationCodeExpiryTime(expiryTime);

            // Save user to the database
            userRepository.save(user);

            // Send OTP to user via email
            sendOTPForEmailVerification(user.getEmail(), otp);

            // Generate JWT
            String token = jwtUtils.generateJwt(user);

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", token);

            apiResponse.setData(data);
            return apiResponse;
        }
        else {
            apiResponse.setError("User already exists or is already verified");
            return apiResponse;
        }
    }


    //Method to generate OTP
    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);

        // Generate OTP characters randomly
        for (int i = 0; i < OTP_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            otp.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }
        return otp.toString();
    }

    // Method to calculate OTP expiry time
    private Date calculateExpiryTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, OTP_EXPIRY_DURATION_MINUTES);
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    //Method for sending OTP to user via email
    private void sendOTPForEmailVerification(String email,String otp){
        String subject="Email verification";
        String body="your verification otp is:"+otp;

        emailService.sendEmail(email,subject,body);
    }

    // Method to verify Email Using OTP
    public APIResponse EmailVerificationUsingOTP(String Email, String userEnteredOTP) {
        APIResponse apiResponse = new APIResponse();

        User user = userRepository.findByEmail(Email);
        if (user != null && user.getVerificationCode() != null && userEnteredOTP.equals(user.getVerificationCode())) {
            // Check if OTP has expired
            if (user.getVerificationCodeExpiryTime().after(new Date(System.currentTimeMillis()))) {
                // If OTP is valid and not expired
                user.setVerified(true);
                userRepository.save(user);
                apiResponse.setStatus(HttpStatus.OK.value());
                apiResponse.setData("User verified successfully");
            } else {
                apiResponse.setError("OTP has expired");
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            }
        } else {
            apiResponse.setError("Invalid OTP");
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return apiResponse;
    }

    public APIResponse Login(LoginRequestDTO loginRequestDTO) {
        APIResponse apiResponse = new APIResponse();

        //Check whether the user exist with given email and password
        User isUser = userRepository.findOneByEmailIgnoreCaseAndPassword(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());

        if (isUser == null) {
            apiResponse.setError("Invalid Credentials!");
            return apiResponse;
        }

        // Generate JWT
        String token = jwtUtils.generateJwt(isUser);

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken",token);

        apiResponse.setData(data);

        return apiResponse;
    }


    public APIResponse ForgotPassword(String email) {
        APIResponse apiResponse=new APIResponse();
        User user=userRepository.findByEmail(email);
        if (user == null) {
            apiResponse.setError("User not found");
        }else{
            String otp=generateOTP();
            user.setVerificationCode(otp);

            Date expiryTime = calculateExpiryTime();
            user.setVerificationCodeExpiryTime(expiryTime);

            user = userRepository.save(user);

            sendOTPForEmailVerification(user.getEmail(),otp);

            EmailVerificationUsingOTP(user.getEmail(),otp);
        }
        return apiResponse;
    }

    public APIResponse ChangePassword(String email, String password) {
        APIResponse apiResponse=new APIResponse();
        User user = userRepository.findByEmail(email);
        user.setPassword(password);
        userRepository.save(user);
        apiResponse.setData("password changed successfully");
        return apiResponse;
    }

    public APIResponse viewProfile(String email) {
        APIResponse apiResponse = new APIResponse();

        try {
            User existingUser = userRepository.findByEmail(email);

            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " not found");
            }

            ViewProfileResponseDTO viewProfileResponseDTO = new ViewProfileResponseDTO();
            viewProfileResponseDTO.setUserId(existingUser.getUserId());
            viewProfileResponseDTO.setUserName(existingUser.getUserName());
            viewProfileResponseDTO.setEmail(existingUser.getEmail());

            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData(viewProfileResponseDTO);
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setError("Error fetching user profile");
            e.printStackTrace();
            return apiResponse;
        }
    }

    public APIResponse editProfile(String email, EditProfileDTO editProfileDTO) {
        APIResponse apiResponse = new APIResponse();

        try {
            User user = userRepository.findByEmail(email);

            if (user == null) {
                throw new UserNotFoundException("User with email " + email + " not found");
            }

            user.setUserName(editProfileDTO.getUserName());

            if (!user.getEmail().trim().equalsIgnoreCase(editProfileDTO.getEmail().trim())) {
                user.setEmail(editProfileDTO.getEmail());

                user.setVerified(false);

                String otp = generateOTP();
                user.setVerificationCode(otp);

                Date expiryTime = calculateExpiryTime();
                user.setVerificationCodeExpiryTime(expiryTime);

                sendOTPForEmailVerification(user.getEmail(), otp);

                // Generate JWT
                String token = jwtUtils.generateJwt(user);

                Map<String, Object> data = new HashMap<>();
                data.put("accessToken", token);
                apiResponse.setData(data);

                //Again have to verify the email
            }

            user.onUpdate();

            userRepository.save(user);

            apiResponse.setStatus(HttpStatus.OK.value());

            return apiResponse;
        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setError("Error on editing user profile");
            e.printStackTrace();
            return apiResponse;
        }

    }
}
