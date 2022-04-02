package top.greatbiscuit.hyxk.service;

/**
 * 注册
 *
 * @Author: GreatBiscuit
 * @Date: 2022/2/19 17:07
 */
public interface RegisterService {

    /**
     * 注册时通过邮箱获取验证码
     *
     * @param email
     * @return
     */
    String getVerificationCode(String email);

    /**
     * 注册方法
     *
     * @param username
     * @param password
     * @param email
     * @param code     验证码
     * @return
     */
    String toRegister(String username, String password, String email, String code);

}
