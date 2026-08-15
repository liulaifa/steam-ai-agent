package com.baozi.steamedCashierService.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baozi.steamedCashierService.mapper.CashierMapper;
import com.baozi.steamedCashierService.service.CashierService;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.constant.StatusConstant;
import com.baozi.steamedCommon.context.CashierContext;
import com.baozi.steamedCommon.domian.dto.CashierAddDTO;
import com.baozi.steamedCommon.domian.dto.CashierPageDTO;
import com.baozi.steamedCommon.domian.dto.CashierUpdateDTO;
import com.baozi.steamedCommon.domian.dto.LoginDTO;
import com.baozi.steamedCashierService.entity.Cashier;
import com.baozi.steamedCommon.exception.BusinessException;
import com.baozi.steamedCommon.util.DateUtils;
import com.baozi.steamedCommon.util.IsLoginUtil;
import com.baozi.steamedCommon.util.JwtUtil;
import com.baozi.steamedCommon.domian.vo.CashierListVO;
import com.baozi.steamedCommon.domian.vo.CashierVO;
import com.baozi.steamedCommon.domian.vo.LoginVO;
import com.baozi.steamedCommon.domian.vo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class CashierServiceImpl extends ServiceImpl<CashierMapper,Cashier> implements CashierService {


    private final CashierMapper cashierMapper;


    /**
     * 收银员列表查询
     */
    public PageResult<CashierListVO> getCashierList(CashierPageDTO cashierPageDTO) {
        //0.登录校验
        IsLoginUtil.isLogin();
        // 1. 构建查询条件
        LambdaQueryWrapper<Cashier> wrapper = new LambdaQueryWrapper<>();
        // 添加关键字查询条件
        if (!StrUtil.isBlank(cashierPageDTO.getKeyword())) {
            String keyword = cashierPageDTO.getKeyword().trim();
            wrapper.and(w -> w
                    .like(Cashier::getUsername, keyword)
                    .or()
                    .like(Cashier::getRealName, keyword)
            );
        }
        //添加排序条件
        wrapper.orderByDesc(Cashier::getCreateTime);

        // 2. 分页查询
        Page<Cashier> page = new Page<>(cashierPageDTO.getPage(), cashierPageDTO.getPageSize());
        Page<Cashier> pageResult = page(page, wrapper);

        //查询为空时返回空的页面
        if (pageResult.getRecords().isEmpty()) {
            return PageResult.<CashierListVO>builder()
                    .total(0L)
                    .page(cashierPageDTO.getPage())
                    .pageSize(cashierPageDTO.getPageSize())
                    .pages(0L)
                    .list(new ArrayList<>())
                    .build();
        }
        //3. 拷贝数据
        List<CashierListVO> cashierListVOS = BeanUtil.copyToList(pageResult.getRecords(), CashierListVO.class);
        // 4. 返回分页结果
        return PageResult.<CashierListVO>builder()
                .total(pageResult.getTotal())
                .page(cashierPageDTO.getPage())
                .pageSize(cashierPageDTO.getPageSize())
                .pages(pageResult.getPages())
                .list(cashierListVOS)
                .build();
    }

    /**
     * 根据ID查询收银员
     */
    public CashierVO getCashierById(Long id) {
        //登录校验
        IsLoginUtil.isLogin();
        Cashier cashier = getById(id);
        return BeanUtil.copyProperties(cashier, CashierVO.class);
    }

    /**
     * 新增收银员
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCashier(CashierAddDTO cashierAddDTO) {
        //登录校验
        IsLoginUtil.isLogin();
        // 1. 校验基本信息校验
        check(cashierAddDTO.getUsername(),cashierAddDTO.getRealName(),cashierAddDTO.getPhone());

        // 2. 检查账号是否重复
        Long count = lambdaQuery()
                .eq(Cashier::getUsername, cashierAddDTO.getUsername().trim())
                .count();
        if (count > 0) {
            throw new BusinessException(MessageConstant.USER_IS_EXIST);//账号已存在
        }

        // 3. 密码处理：不传则默认123456

        String password = cashierAddDTO.getPassword();
        if (StrUtil.isBlank(password)) {
            password = "123456";
        }
        // 4. 创建收银员
        Cashier cashier = BeanUtil.copyProperties(cashierAddDTO, Cashier.class);
        String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());
        cashier.setPassword(hashpw);
        cashierMapper.insert(cashier);
        log.info("【新增收银员成功：{}】", cashierAddDTO.getUsername());
    }

    /**
     * 修改收银员
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCashier(Long id, CashierUpdateDTO dto) {
        //登录校验
        IsLoginUtil.isLogin();
        //查询收银员信息
        Cashier cashier = cashierMapper.selectById(id);
        if (cashier == null) {
            throw new BusinessException(MessageConstant.USER_IS_EXIST);//用户不存在
        }

        // 校验账号
        check(dto.getUsername(),dto.getRealName(),dto.getPhone());

        // 检查账号是否重复（排除自己）
        Long count = lambdaQuery()
                .eq(Cashier::getUsername, dto.getUsername().trim())
                .ne(Cashier::getId, id)
                .count();
        if (count > 0) {
            throw new BusinessException(MessageConstant.USER_IS_EXIST);//账号已存在
        }

        // 更新基本信息
        cashier.setUsername(dto.getUsername().trim());
        cashier.setRealName(dto.getRealName().trim());
        cashier.setPhone(dto.getPhone().trim());
        cashier.setStatus(dto.getStatus() != null ? dto.getStatus() : cashier.getStatus());

        // 密码：传了才更新
        String password = dto.getPassword();
        if (!StrUtil.isBlank(password)) {
            cashier.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        }

        cashierMapper.updateById(cashier);

        log.info("【修改收银员成功：真实姓名：{}】", dto.getRealName());
    }

    /**
     * 重置密码
     */
    public void resetPassword(Long id) {
        //登录校验
        IsLoginUtil.isLogin();

        Cashier cashier = cashierMapper.selectById(id);
        if (cashier == null) {
            throw new BusinessException(MessageConstant.USER_IS_EXIST);//用户不存在
        }

        String defaultPassword = "123456";
        String hashpw = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());
        cashier.setPassword(hashpw);

        cashierMapper.updateById(cashier);

        log.info("【重置密码成功：真实姓名：{}】", cashier.getRealName());
    }

    /**
     * 状态切换
     */
    public Integer toggleStatus(Long id) {
        // 1. 获取当前登录收银员ID
        Long currentId = CashierContext.getCurrentId();
        IsLoginUtil.isLogin(currentId);

        // 2. 不能修改自己的状态
        if (currentId.equals(id)) {
            throw new BusinessException(MessageConstant.TRY_AGAIN);//不能修改自己的状态
        }

        // 3. 查询收银员
        Cashier cashier = cashierMapper.selectById(id);
        if (cashier == null) {
            throw new BusinessException(MessageConstant.USER_IS_EXIST);
        }

        // 4. 状态取反
        Integer newStatus = StatusConstant.ENABLE.equals(cashier.getStatus()) ? StatusConstant.DISABLE : StatusConstant.ENABLE;
        cashier.setStatus(newStatus);
        cashierMapper.updateById(cashier);

        log.info("【收银员状态切换：真实姓名={}, 新状态={}】", cashier.getRealName(), newStatus);
        return newStatus;
    }

    /**
     * 删除收银员
     */
    public void deleteCashier(Long id) {
        // 1. 获取当前登录收银员ID
        Long currentId = CashierContext.getCurrentId();
        IsLoginUtil.isLogin(currentId);

        // 2. 不能删除自己
        if (currentId.equals(id)) {
            throw new BusinessException(MessageConstant.DELETE_IS_ERROR);//不能删除自己
        }

        // 3. 查询收银员
        Cashier cashier = cashierMapper.selectById(id);
        if (cashier == null) {
            throw new BusinessException(MessageConstant.USER_IS_EXIST);//用户不存在
        }

        // 4. 必须是离职状态才能删除
        if (StatusConstant.ENABLE.equals(cashier.getStatus())) {
            throw new BusinessException(MessageConstant.ENABLE_USER);//请先停用该收银员
        }

        // 5. 逻辑删除
        cashierMapper.deleteById(cashier);

        log.info("【逻辑删除收银员成功： 用户名={}，真实姓名={}】", cashier.getUsername(), cashier.getRealName());
    }

    // 校验
    private void check(String username, String realName, String phone){
        // 1. 校验必填字段
        if (StrUtil.isBlank(username)) {
            throw new BusinessException(MessageConstant.USERNAME_IS_EMPTY);//账号不能为空
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            throw new BusinessException(MessageConstant.ONLY_LETTERS_AND_NUMBERS);//登录账号只能包含字母和数字
        }
        if (username.length() > 20) {
            throw new BusinessException(MessageConstant.USERNAME_TOLONG);//登录账号不能超过20个字符
        }

        if (StrUtil.isBlank(realName)) {
            throw new BusinessException(MessageConstant.REALNAME_IS_EMPTY);//真实姓名不能为空
        }
        if (realName.length() > 10) {
            throw new BusinessException(MessageConstant.REALNAME_TOLONG);//真实姓名不能超过10个字符
        }

        if (StrUtil.isBlank(phone)) {
            throw new BusinessException(MessageConstant.PHONE_IS_EMPTY);//手机号不能为空
        }
        if (!phone.matches("^1[0-9]{10}$")) {
            throw new BusinessException(MessageConstant.PHONE_NOT_MATCH);//手机号格式不正确
        }
    }
}
