package com.inther.services;

import com.inther.beans.AuthorityUtilityBean;
import com.inther.beans.ResponseBean;
import com.inther.beans.ServiceUtilityBean;
import com.inther.entities.implementation.UserAuthorityEntity;
import com.inther.entities.implementation.UserEntity;
import com.inther.exceptions.*;
import com.inther.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService
{
    private final AuthorityUtilityBean authorityUtilityBean;
    private final ServiceUtilityBean serviceUtilityBean;
    private final UserRepository userRepository;
    private final ResponseBean responseBean;
    private final HttpHeaders httpHeaders;

    private UserEntity nestedFieldValueCheck(UserEntity userEntity) throws Exception
    {
        for (UserAuthorityEntity userAuthorityEntity : userEntity.getUserAuthorities())
        {
            if (!userAuthorityEntity.getEmail().equals(userEntity.getEmail()))
            {
                throw new NestedFieldValueException("Authority email does not match user email");
            }
        }
        return userEntity;
    }

    public ResponseBean putUser(UserEntity userEntity) throws Exception
    {
        if (authorityUtilityBean.validateAdminAuthority())
        {
            Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(userEntity.getEmail());
            if (!optionalUserEntity.isPresent())
            {
                userRepository.save(nestedFieldValueCheck(userEntity));
                responseBean.setHeaders(httpHeaders);
                responseBean.setStatus(HttpStatus.CREATED);
                responseBean.setResponse("User " + userEntity.getEmail() + " successfully added");
            }
            else
            {
                throw new DuplicatedEntryException("User with same email already exists");
            }
        }
        else
        {
            throw new AccessDeniedException("Access denied for you authority");
        }
        return responseBean;
    }

    public ResponseBean getUser(String email) throws Exception
    {
        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(email);
        if (optionalUserEntity.isPresent())
        {
            responseBean.setHeaders(httpHeaders);
            responseBean.setStatus(HttpStatus.OK);
            responseBean.setResponse(optionalUserEntity.get());
        }
        else
        {
            throw new NotFoundEntryException("User " + email + " not found");
        }
        return responseBean;
    }

    public ResponseBean patchUser(UserEntity userEntity) throws Exception
    {
        if (authorityUtilityBean.getCurrentAuthenticationEmail().equals(userEntity.getEmail()) || authorityUtilityBean.validateAdminAuthority())
        {
            Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(userEntity.getEmail());
            if (optionalUserEntity.isPresent())
            {
                userRepository.save(serviceUtilityBean.patchEntity(optionalUserEntity.get(), userEntity));
                responseBean.setHeaders(httpHeaders);
                responseBean.setStatus(HttpStatus.OK);
                responseBean.setResponse("User " + userEntity.getEmail() + " successfully patched");
            }
            else
            {
                throw new NotFoundEntryException("User " + userEntity.getEmail() + " not found");
            }
        }
        else
        {
            throw new AccessDeniedException("Access denied for you authority");
        }
        return responseBean;
    }

    public ResponseBean deleteUser(String email) throws Exception
    {

        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(email);
        if (optionalUserEntity.isPresent())
        {
            if (!authorityUtilityBean.getCurrentAuthenticationEmail().equals(email) && authorityUtilityBean.validateAdminAuthority())
            {
                userRepository.deleteUserEntityByEmail(email);
                responseBean.setHeaders(httpHeaders);
                responseBean.setStatus(HttpStatus.OK);
                responseBean.setResponse("User " + email + " successfully deleted");
            }
            else if (authorityUtilityBean.getCurrentAuthenticationEmail().equals(email) && authorityUtilityBean.validateAdminAuthority())
            {
                throw new SelfDestructionException("You is about to delete yourself");
            }
            else
            {
                throw new AccessDeniedException("Access denied for you authority");
            }
        }
        else
        {
            throw new NotFoundEntryException("User " + email + " not found");
        }
        return responseBean;
    }

    @Autowired
    public UserService(AuthorityUtilityBean authorityUtilityBean, ServiceUtilityBean serviceUtilityBean, UserRepository userRepository, ResponseBean responseBean, HttpHeaders httpHeaders)
    {
        this.authorityUtilityBean = authorityUtilityBean;
        this.serviceUtilityBean = serviceUtilityBean;
        this.userRepository = userRepository;
        this.responseBean = responseBean;
        this.httpHeaders = httpHeaders;
    }
}