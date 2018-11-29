package com.rsa.rsademo;

import com.rsa.rsademo.RSATEST.utils.Base64Utils;
import com.rsa.rsademo.RSATEST.utils.RSAUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RsademoApplicationTests {

    @Test
    public void contextLoads() throws Exception {
        HashMap<String, Object> map = (HashMap<String, Object>) RSAUtils.genKeyPair();//获取了密钥对
        String publickey = RSAUtils.getPublicKey(map);//获取公钥
        String preatekey = RSAUtils.getPrivateKey(map);//获取私钥
        System.out.println("公钥-" + publickey);
        System.out.println("私钥-" + preatekey);
        String a = RSAUtils.encryptedDataOnJava("123", publickey);//公钥加密
        System.out.println("公钥加密后是-"+a);
        String str = RSAUtils.decryptDataOnJava(a, preatekey);//私钥解密
        System.out.println("私钥解密后是-"+str);
    }
    @Test
    public void contextLoads1() throws Exception {
        HashMap<String, Object> map = (HashMap<String, Object>) RSAUtils.genKeyPair();//获取了密钥对
        String publickey = RSAUtils.getPublicKey(map);//获取公钥
        String preatekey = RSAUtils.getPrivateKey(map);//获取私钥
        System.out.println("公钥-" + publickey);
        System.out.println("私钥-" + preatekey);
        String password="asdasdas";
        byte[] bytes= password.getBytes();//获取密码数据的字节数组
        byte[] encodedData = RSAUtils.encryptByPrivateKey(bytes, preatekey);//利用私钥对数据加密
        String a = RSAUtils.sign(encodedData,preatekey);//加密后进行数字签名
        System.out.println("数字签名-"+a);
        boolean str = RSAUtils.verify(encodedData,publickey,a);//利用公钥对签名校验
        System.out.println("校验结果是-"+str);
    }

}
