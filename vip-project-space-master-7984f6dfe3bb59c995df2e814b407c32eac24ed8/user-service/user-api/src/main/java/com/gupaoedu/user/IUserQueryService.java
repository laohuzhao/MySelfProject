package com.gupaoedu.user;


import com.gupaoedu.user.dto.UserQueryRequest;
import com.gupaoedu.user.dto.UserQueryResponse;

/**

 */
public interface IUserQueryService {


    /**
     * 根据用户id来查询用户信息
     * @param request
     * @return
     */
    UserQueryResponse getUserById(UserQueryRequest request);

}
