package com.user.userAuth.services;

import com.user.userAuth.exceptions.InvalidCredentialsException;
import com.user.userAuth.models.Token;
import com.user.userAuth.models.User;
import com.user.userAuth.repositories.TokenRepository;
import com.user.userAuth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public User signUp(String name, String email, String password){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Token login(String email, String password) throws InvalidCredentialsException {
       Optional<User> user = userRepository.findByEmail(email);
       if(user.isEmpty()){
           throw new InvalidCredentialsException();
       }

        if (!bCryptPasswordEncoder.matches(password, user.get().getHashPassword())) {
            throw new InvalidCredentialsException();
        }

       Token token = new Token();
       token.setUser(user.get());
       token.setValue(UUID.randomUUID().toString());
       token.setExpiryDate(get30DaysLaterDate());
       return tokenRepository.save(token);
    }

    private Date  get30DaysLaterDate() {
        LocalDate currentDate = LocalDate.now().plusDays(30);
         return Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void logout(String token) {
       Optional<Token> optionalToken = tokenRepository.findByValueAndIsDeletedEquals(token, false);
       if(optionalToken.isEmpty()){
           return;
       }
        optionalToken.get().setDeleted(true);
        tokenRepository.save(optionalToken.get());
    }
}
