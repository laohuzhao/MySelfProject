package com.rsa.rsademo.RSATEST.controller;

import com.rsa.rsademo.RSATEST.utils.RSAUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试加密算法和token验证
 */
@RestController
public class TestController {
    String pukey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBXq4UBBMj/Uoqk0zd5I6+9m33fOjfFQQr33vw\n" +
            "RqAJ0gAX7EfoF+/cbJsYXWidenMmuaNpsLUaGZp5hTj2ALazStNqFA3+ep8O6YrtIvXoTOEZ46lP\n" +
            "fKzungXYieFA3/vaj0rL+EITo8JbeVf8ugGZBWofcB54srVMlw4nY6NRowIDAQAB";
    String prkey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIFerhQEEyP9SiqTTN3kjr72bfd8\n" +
            "6N8VBCvfe/BGoAnSABfsR+gX79xsmxhdaJ16cya5o2mwtRoZmnmFOPYAtrNK02oUDf56nw7piu0i\n" +
            "9ehM4RnjqU98rO6eBdiJ4UDf+9qPSsv4QhOjwlt5V/y6AZkFah9wHniytUyXDidjo1GjAgMBAAEC\n" +
            "gYBIlfnwCxU+NSh+2OTg/xNEe/nZSYFTYXRUtXDRsJ6sw+H/ijgSNhQDxgADXK2M5h5j1PamH1qN\n" +
            "iV4N3rzF8kKPaqp5pI3Du4akhXUrro0kcZRI/UFbuXMA2muprEY0sk91WHIyJfKK422731utehG4\n" +
            "RS7IA7LyyUBgRQwA8qFX2QJBAM2GlIXzm/rNdyLWlewhctyA20LUmVf1D+djQmO+YQhCN9mrmqfF\n" +
            "9KvazshFBeVL6jCNCa5BHlETH2E5zZvYSX8CQQChJCyGOB/dJ99qIDWFHnGsqXBx0i1ui5bpSX44\n" +
            "ko1aa/2euhxPG8gm/4ZOkJZ6fNe9rgU7rBYlNYfk+lCHzaHdAkA1G8yvXIb25Tce6oJtNSuALU/7\n" +
            "UgKaBvpoGW8jZSOfxbS267AYIIAcFHuQpr1iRh6rExboT21tF9ro/c3Ssq4zAkEAn+FES+djtgnZ\n" +
            "oXJTp8CNCMMUzdm7OfNFyDEjCv5dQVnkSZhefh8Sd58PouPSabV0WL5QcuRLFKAINSgmvpdIAQJA\n" +
            "I5uLGtO2O5WcwwPaT6YVaJL5YZYizEWqpszHMORm10vOnb4Gj18dA1jAnN8WvF581tFGKNHVvhT2\n" +
            "n3lkfzUhwA==";
/*这是前端的写法，证明是可以使用的
<script type="text/javascript">a
   function f() {
        var encrypt = new JSEncrypt();
        encrypt.setPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcnVM7Nvrme1dOlZUq7YKrk+A0/rgH00h+8sSF\n" +
            "HLBZ3+cPzd+y+dDrcDmLnd3xsszmy6wmrDU86MijKmXvuH33eYo0XPkTg6BaewMJXpOuYhDVRR5k\n" +
            "3QgkLjjU9L/0X4/yH8rZ4axINMWA3NHHZYlhr4ms1/VeLfQB+d7718f6AQIDAQAB");
        var encryptData = encrypt.encrypt("123");//加密后的字符串
        $("#aaa").html(encryptData);

    };
</script>
<button onclick="f()">aaa</button>
<p id="aaa"></p>
<script src="../js/jquery-1.12.4.js"></script>
<script src="jsencrypt.min.js"></script>*/



    @PostMapping(value = "/login")
    public String login() {

        return "登录成功";
    }

    @PostMapping(value = "/token")
    public String login1() {

        return "验证成功";
    }

    @PostMapping(value = "/rsa")
    public String loginRAS(String username, String password) {
        String pwd = RSAUtils.decryptDataOnJava(password, prkey);
        System.out.println(pwd+"-------------");
        if (pwd.equals("123")) {
            return "ok";
        } else {
            return "no";
        }
    }
}
