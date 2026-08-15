package com.baozi.steamedCashierService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baozi.steamedCommon.domian.dto.CashierAddDTO;
import com.baozi.steamedCommon.domian.dto.CashierPageDTO;
import com.baozi.steamedCommon.domian.dto.CashierUpdateDTO;
import com.baozi.steamedCommon.domian.dto.LoginDTO;
import com.baozi.steamedCashierService.entity.Cashier;
import com.baozi.steamedCommon.domian.vo.CashierListVO;
import com.baozi.steamedCommon.domian.vo.CashierVO;
import com.baozi.steamedCommon.domian.vo.LoginVO;
import com.baozi.steamedCommon.domian.vo.PageResult;

public interface CashierService extends IService<Cashier> {

    /**
     * 收银员列表查询
     */
    PageResult<CashierListVO> getCashierList(CashierPageDTO cashierPageDTO);

    /**
     * 根据ID查询收银员
     */
    CashierVO getCashierById(Long id);

    /**
     * 新增收银员
     */
    void addCashier(CashierAddDTO dto);

    /**
     * 修改收银员
     */
    void updateCashier(Long id, CashierUpdateDTO dto);

    /**
     * 重置密码
     */
    void resetPassword(Long id);

    /**
     * 状态切换
     */
    Integer toggleStatus(Long id);

    /**
     * 删除收银员
     */
    void deleteCashier(Long id);
}
