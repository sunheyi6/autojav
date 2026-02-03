package com.autojav.core.license;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 许可证验证器
 * 基于 AES 加密 + 签名验证的本地许可证验证
 */
@Slf4j
public class LicenseValidator {

    // 密钥（实际生产环境应从安全渠道获取或拆分存储）
    private static final String SECRET_KEY = "AutoJava2026#License";
    
    // 签名前缀
    private static final String SIGNATURE_PREFIX = "AJV:";
    
    /**
     * 验证许可证密钥格式和签名
     * @param licenseKey 许可证密钥
     * @return 验证结果
     */
    public static ValidationResult validate(String licenseKey) {
        if (licenseKey == null || licenseKey.trim().isEmpty()) {
            return ValidationResult.fail("许可证密钥为空");
        }
        
        try {
            // 解密许可证数据
            String decrypted = decrypt(licenseKey);
            
            // 验证签名前缀
            if (!decrypted.startsWith(SIGNATURE_PREFIX)) {
                return ValidationResult.fail("无效的许可证格式");
            }
            
            // 解析许可证数据
            // 格式: AJV:版本类型:过期时间:机器指纹:签名
            String[] parts = decrypted.substring(SIGNATURE_PREFIX.length()).split(":");
            if (parts.length < 4) {
                return ValidationResult.fail("许可证数据不完整");
            }
            
            String versionTypeStr = parts[0];
            String expireTimeStr = parts[1];
            String machineFingerprint = parts[2];
            String signature = parts[3];
            
            // 验证版本类型
            VersionType versionType;
            try {
                versionType = VersionType.valueOf(versionTypeStr);
            } catch (IllegalArgumentException e) {
                return ValidationResult.fail("未知的许可证版本");
            }
            
            // 验证过期时间
            LocalDateTime expireTime = LocalDateTime.parse(expireTimeStr, 
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            if (LocalDateTime.now().isAfter(expireTime)) {
                return ValidationResult.fail("许可证已过期");
            }
            
            // 验证机器指纹（如果绑定了机器）
            if (!machineFingerprint.isEmpty() && !machineFingerprint.equals("ANY")) {
                String currentFingerprint = getMachineFingerprint();
                if (!machineFingerprint.equals(currentFingerprint)) {
                    return ValidationResult.fail("许可证与当前设备不匹配");
                }
            }
            
            // 验证签名
            String dataToSign = versionTypeStr + ":" + expireTimeStr + ":" + machineFingerprint;
            String expectedSignature = generateSignature(dataToSign);
            if (!expectedSignature.equals(signature)) {
                return ValidationResult.fail("许可证签名无效，可能已被篡改");
            }
            
            // 验证通过，构建许可证对象
            License license = new License();
            license.setLicenseId(licenseKey.substring(0, Math.min(16, licenseKey.length())));
            license.setVersionType(versionType);
            license.setStartTime(LocalDateTime.now().minusDays(1)); // 假设昨天开始
            license.setExpireTime(expireTime);
            license.setMachineFingerprint(machineFingerprint);
            license.setPermissions(new FeaturePermissions(versionType));
            license.setStatus(License.LicenseStatus.ACTIVE);
            
            return ValidationResult.success(license);
            
        } catch (Exception e) {
            log.error("验证许可证失败", e);
            return ValidationResult.fail("验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成许可证密钥（用于后台生成）
     * @param versionType 版本类型
     * @param expireTime 过期时间
     * @param machineFingerprint 机器指纹（为空表示不绑定）
     * @return 许可证密钥
     */
    public static String generateLicenseKey(VersionType versionType, LocalDateTime expireTime, 
                                            String machineFingerprint) {
        try {
            String fp = machineFingerprint != null ? machineFingerprint : "ANY";
            
            // 生成签名
            String dataToSign = versionType.name() + ":" + 
                    expireTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ":" + fp;
            String signature = generateSignature(dataToSign);
            
            // 构建完整数据
            String licenseData = SIGNATURE_PREFIX + versionType.name() + ":" + 
                    expireTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ":" + 
                    fp + ":" + signature;
            
            // 加密
            return encrypt(licenseData);
            
        } catch (Exception e) {
            log.error("生成许可证失败", e);
            throw new RuntimeException("生成许可证失败", e);
        }
    }
    
    /**
     * 生成试用许可证（7天团队版）
     * @return 试用许可证密钥
     */
    public static String generateTrialLicense() {
        LocalDateTime expireTime = LocalDateTime.now().plusDays(7);
        return generateLicenseKey(VersionType.TEAM, expireTime, "ANY");
    }
    
    /**
     * 获取机器指纹
     * 基于用户名和机器名生成简单指纹
     */
    public static String getMachineFingerprint() {
        try {
            String userName = System.getProperty("user.name");
            String osName = System.getProperty("os.name");
            String machineName = java.net.InetAddress.getLocalHost().getHostName();
            
            String combined = userName + "@" + machineName + "#" + osName;
            return hash(combined).substring(0, 16);
        } catch (Exception e) {
            log.warn("获取机器指纹失败，使用默认指纹", e);
            return "UNKNOWN";
        }
    }
    
    /**
     * 加密字符串
     */
    private static String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * 解密字符串
     */
    private static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    
    /**
     * 生成签名
     */
    private static String generateSignature(String data) {
        return hash(data + SECRET_KEY).substring(0, 16);
    }
    
    /**
     * 哈希计算
     */
    private static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 获取密钥字节
     */
    private static byte[] getKeyBytes() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        // AES-128 需要16字节密钥
        byte[] key = new byte[16];
        System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 16));
        return key;
    }
    
    /**
     * 验证结果
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final License license;
        
        private ValidationResult(boolean valid, String message, License license) {
            this.valid = valid;
            this.message = message;
            this.license = license;
        }
        
        public static ValidationResult success(License license) {
            return new ValidationResult(true, "验证成功", license);
        }
        
        public static ValidationResult fail(String message) {
            return new ValidationResult(false, message, null);
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public License getLicense() { return license; }
    }
}
