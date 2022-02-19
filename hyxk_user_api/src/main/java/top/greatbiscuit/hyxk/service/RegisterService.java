package top.greatbiscuit.hyxk.service;

import java.util.Map;

/**
 * 注册
 *
 * @Author: GreatBiscuit
 * @Date: 2022/2/19 17:07
 */
public interface RegisterService {

    Map toRegister(String username, String password, String email);

    String activation(Integer userId, String code);

}
