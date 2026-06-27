package com.major.project.airBnbApp.security;

import com.major.project.airBnbApp.dto.LoginDto;
import com.major.project.airBnbApp.dto.SignUpRequestDto;
import com.major.project.airBnbApp.dto.UserDto;
import com.major.project.airBnbApp.entity.User;
import com.major.project.airBnbApp.entity.enums.Role;
import com.major.project.airBnbApp.exception.ResourceNotFoundException;
import com.major.project.airBnbApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {

        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);
        if(user != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        }
        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser, UserDto.class);
    }

    public String[] login(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ));
        User user =(User) authentication.getPrincipal();

        String[] arr = new String[2];
        assert user != null;
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public String refreshToken(String refreshToken){
        Long id = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not Found with "+ id));
        return jwtService.generateAccessToken(user);
    }
}





























