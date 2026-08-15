package com.baozi.steamedCashierService.controller;

import com.baozi.steamedCashierService.service.CashierService;
import com.baozi.steamedCommon.annotation.Log;
import com.baozi.steamedCashierService.entity.Cashier;
import com.baozi.steamedCommon.domian.vo.*;
import com.baozi.steamedCommon.domian.dto.CashierAddDTO;
import com.baozi.steamedCommon.domian.dto.CashierPageDTO;
import com.baozi.steamedCommon.domian.dto.CashierUpdateDTO;
import com.baozi.steamedCommon.domian.dto.LoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "收银员模块")
public class CashierController {

    private final CashierService cashierService;

    @Operation(summary = "收银员列表查询")
    @PostMapping("/list")
    public Result<PageResult<CashierListVO>> getCashierList(@RequestBody CashierPageDTO cashierPageDTO) {
        PageResult<CashierListVO> pageResult = cashierService.getCashierList(cashierPageDTO);
        return Result.success(pageResult);
    }

    @Operation(summary = "根据ID批量查询收银员")
    @GetMapping("/batch")
    public Result<List<Cashier>> getCashiersByIds(@RequestParam List<Long> ids) {
        return Result.success(cashierService.listByIds(ids));
    }

    @Operation(summary = "根据ID查询收银员")
    @GetMapping("/{id}")
    public Result<CashierVO> getCashierById(@PathVariable Long id) {
        CashierVO cashier = cashierService.getCashierById(id);
        return Result.success(cashier);
    }

    @Operation(summary = "新增收银员")
    @PostMapping("/add")
    @Log("新增收银员：#dto.username")
    public Result<Void> addCashier(@RequestBody CashierAddDTO dto) {
        cashierService.addCashier(dto);
        return Result.success(null);
    }

    @Operation(summary = "修改收银员")
    @PutMapping("/{id}")
    @Log("修改收银员：#dto.username")
    public Result<Void> updateCashier(@PathVariable Long id, @RequestBody CashierUpdateDTO dto) {
        cashierService.updateCashier(id, dto);
        return Result.success(null);
    }

    @Operation(summary = "重置密码")
    @PutMapping("/password/{id}")
    @Log("重置密码：ID=#id")
    public Result<Void> resetPassword(@PathVariable Long id) {
        cashierService.resetPassword(id);
        return Result.success(null);
    }

    @Operation(summary = "状态切换")
    @PutMapping("/status/{id}")
    @Log("收银员状态切换：ID=#id")
    public Result<Integer> toggleStatus(@PathVariable Long id) {
        Integer newStatus = cashierService.toggleStatus(id);
        return Result.success(newStatus);
    }

    @Operation(summary = "删除收银员")
    @DeleteMapping("/{id}")
    @Log("删除收银员：ID=#id")
    public Result<Void> deleteCashier(@PathVariable Long id) {
        cashierService.deleteCashier(id);
        return Result.success(null);
    }
}
