package com.example;

import java.util.List;
import java.util.ArrayList;

/**
 * 空指针异常示例 - 展示各种NPE风险场景
 */
public class NullPointerExample {
    
    private String name;
    private List<String> items;
    
    /**
     * 场景1：直接调用参数方法（有风险）
     */
    public void riskyMethod(String input) {
        // ❌ 风险：input可能为null
        int len = input.length();
        System.out.println(len);
    }
    
    /**
     * 场景2：安全写法 - 空指针检查
     */
    public void safeMethod(String input) {
        // ✅ 安全：先检查再调用
        if (input != null) {
            int len = input.length();
            System.out.println(len);
        }
    }
    
    /**
     * 场景3：链式调用（有风险）
     */
    public void chainCallRisk(NullPointerExample other) {
        // ❌ 风险：other可能为null，items也可能为null
        int size = other.items.size();
        System.out.println(size);
    }
    
    /**
     * 场景4：成员变量访问（有风险）
     */
    public void fieldAccessRisk() {
        // ❌ 风险：items可能为null
        items.add("item");
    }
    
    /**
     * 场景5：自动装箱NPE风险
     */
    public void autoBoxingRisk(Integer num) {
        // ❌ 风险：num可能为null，自动拆箱会抛NPE
        int value = num;
        System.out.println(value);
    }
    
    /**
     * 场景6：返回null未检查（有风险）
     */
    public void returnNullRisk() {
        String result = findUserName();
        // ❌ 风险：result可能为null
        System.out.println(result.toUpperCase());
    }
    
    private String findUserName() {
        return null; // 可能返回null
    }
}
