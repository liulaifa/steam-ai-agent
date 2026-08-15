package com.baozi.steamedCommon.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;


public class MyMetaObjectHandler implements MetaObjectHandler {


    //创建时自动插入
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "lastLoginTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "hasFlavor", Integer.class, 0);
        this.strictInsertFill(metaObject, "status", Integer.class, 0);
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    //修改时自动插入
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "lastLoginTime", LocalDateTime.class, LocalDateTime.now());
    }
}
