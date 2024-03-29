package top.greatbiscuit.hyxk.service;

/**
 * 登录
 *
 * @Author: GreatBiscuit
 * @Date: 2022/1/12 18:05
 */
public interface LoginService {

    String login(String username, String password);

    String findUsername(String email);

    String getVerificationCode(String email);

    String findPassword(String email, String code, String password);
}
