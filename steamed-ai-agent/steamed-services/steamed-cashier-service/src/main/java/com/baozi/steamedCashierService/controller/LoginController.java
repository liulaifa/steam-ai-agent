package com.baozi.steamedCashierService.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baozi.steamedCashierService.entity.Cashier;
import com.baozi.steamedCashierService.mapper.CashierMapper;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.constant.StatusConstant;
import com.baozi.steamedCommon.domian.dto.LoginDTO;
import com.baozi.steamedCommon.domian.dto.RegisterDTO;
import com.baozi.steamedCommon.domian.vo.LoginVO;
import com.baozi.steamedCommon.domian.vo.Result;
import com.baozi.steamedCommon.exception.BusinessException;
import com.baozi.steamedCommon.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/public")
@Tag(name = "登录模块")
public class LoginController {

    private final CashierMapper cashierMapper;
    private final JwtUtil jwtUtil;


    @Operation(summary = "前台登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 非空校验
        if (StrUtil.isBlank(username)) {
            throw new BusinessException(MessageConstant.USERNAME_IS_EMPTY);//账号不能为空
        }
        if (StrUtil.isBlank(password)) {
            throw new BusinessException(MessageConstant.PASSWORD_IS_EMPTY);//密码不能为空
        }
        // 查询数据库
        Cashier cashier = cashierMapper.selectOne(
                new LambdaQueryWrapper<Cashier>()
                        .eq(Cashier::getUsername, username)
                        .eq(Cashier::getStatus, StatusConstant.ENABLE) //1:在职
        );

        if(cashier!=null)
        {
            // 对密码进行校验BCrypt.checkpw(原密码，加密后的密码)
            if (!BCrypt.checkpw(password, cashier.getPassword())){
                throw new BusinessException(MessageConstant.LOGIN_ERROR);
            }
        }else{
            throw new BusinessException(MessageConstant.USER_IS_EMPTY);
        }

        log.info("登录成功：{}", cashier.getRealName());
        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", cashier.getId());
        claims.put("realName", cashier.getRealName());
        String token = jwtUtil.createToken(cashier.getId(), claims);

        //封装登录结果loginVO
        LoginVO loginVO = new LoginVO();
        loginVO.setId(cashier.getId());
        loginVO.setRealName(cashier.getRealName());
        response.setHeader(HttpHeaders.AUTHORIZATION, token);
        // 返回结果
        return Result.success(loginVO);
    }

    @Operation(summary = "注册用户")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String realName = registerDTO.getRealName();
        String phone = registerDTO.getPhone();
        if (StrUtil.isBlank(username)) {
            throw new BusinessException(MessageConstant.USERNAME_IS_EMPTY);//账号不能为空
        }
        if (StrUtil.isBlank(password)) {
            throw new BusinessException(MessageConstant.PASSWORD_IS_EMPTY);//密码不能为空
        }
        if (StrUtil.isBlank(realName)) {
            throw new BusinessException(MessageConstant.REALNAME_IS_EMPTY);//真实姓名不能为空
        }
        if (StrUtil.isBlank(phone)) {
            throw new BusinessException(MessageConstant.PHONE_IS_EMPTY);//手机号不能为空
        }
        Cashier cashier = cashierMapper.selectOne(
                new LambdaQueryWrapper<Cashier>()
                        .eq(Cashier::getUsername, username)
        );
        if(cashier!=null)
        {
            throw new BusinessException(MessageConstant.USER_IS_EXIST);
        }
        registerDTO.setPassword(BCrypt.hashpw(password));
        cashierMapper.insert(BeanUtil.copyProperties(registerDTO, Cashier.class));
        return Result.success();
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
