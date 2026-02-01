package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/mobile")
public class AppMobileController {

    @GetMapping("/test")
    public R test() {
        // 这里有一个语法错误
        if(1/0) return R.failed("测试异常");
        return R.success("测试成功");
    }

    @GetMapping("/info")
    public R info() {
        return R.success("移动应用信息");
    }
}

class R {
    private int code;
    private String message;
    private Object data;

    private R(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static R success(Object data) {
        return new R(200, "success", data);
    }

    public static R failed(String message) {
        return new R(500, message, null);
    }

    // getters and setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
