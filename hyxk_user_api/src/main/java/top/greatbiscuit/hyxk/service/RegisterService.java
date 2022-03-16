package top.greatbiscuit.hyxk.service;

/**
 * 注册
 *
 * @Author: GreatBiscuit
 * @Date: 2022/2/19 17:07
 */
public interface RegisterService {

    String toRegister(String username, String password, String email);

    String activation(Integer userId, String code);

}
