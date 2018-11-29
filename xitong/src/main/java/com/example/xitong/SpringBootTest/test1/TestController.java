package com.example.xitong.SpringBootTest.test1;

import com.example.xitong.common.pojo.User;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.Valid;
import java.util.List;

public class TestController {
    /*AuthorizeIn实体类，@Valid实现参数验证*/
    @GetMapping("authorize")
    public void authorize(@Valid User authorize, BindingResult result) {
        if (result.hasFieldErrors()) {
            List<FieldError> errorList = result.getFieldErrors();
            //通过断言抛出参数不合法的异常
            errorList.stream().forEach(item -> Assert.isTrue(false, item.getDefaultMessage()));
        }
    }
}
