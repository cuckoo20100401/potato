package org.potato.validation;

import org.potato.util.DTO;
import org.potato.util.Result;
import org.potato.validation.rule.IntegerRule;
import org.potato.validation.rule.StringRule;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Validation组件的两种用法示例
 */
@RestController
public class UserController {

    /**
     * 测试 @RequestParam 接收参数
     *
     * @param title
     * @param age
     * @return
     */
    @RequestMapping("/testRequestParam")
    public Result testRequestParam(
            @RequestParam String title,
            @RequestParam Integer age) {

        //step 校验参数
        //substep Validator组件校验参数
        Result result = Validator.builder()
                .validateString("title", title, StringRule.notNullAndEmpty(), "title不能为空")
                .validateString("title", title, StringRule.lengthEqual(8), "title长度必须等于8")
                .validateString("title", title, StringRule.lengthGreaterThan(10), "title长度必须大于10")
                .validateString("title", title, StringRule.lengthLessThan(20), "title长度必须小于20")
                .validateString("title", title, StringRule.lengthBetween(10, 20), "title长度必须在10和20之间")
                .validateInteger("age", age, IntegerRule.notNull(), "age不能为空")
                .validateInteger("age", age, IntegerRule.lengthEqual(8), "age长度必须等于8")
                .result();
        if (result.isFailure()) {
            return result;
        }
        //substep 自定义校验参数（弥补Validator组件的不足）
        if (title.contains("@")) {
            return Result.failure().message("title不能包含@符号");
        }

        //step 执行业务
        try {
//            userService.process(title, age);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    /**
     * 测试 @RequestBody 接收参数
     *
     * @param dto
     * @return
     */
    @RequestMapping("/testRequestBody")
    public Result testRequestBody(@RequestBody DTO dto) {

        //step 校验参数
        //substep Validator组件校验参数
        Result result = Validator.builder(dto)
                .validateString("title", StringRule.notNullAndEmpty(), "title不能为空")
                .validateString("title", StringRule.lengthGreaterThan(10), "title长度必须大于10")
                .validateString("title", StringRule.lengthLessThan(20), "title长度必须小于20")
                .validateString("title", StringRule.lengthBetween(10, 20), "title长度必须在10和20之间")
                .validateInteger("age", IntegerRule.notNull(), "age不能为空")
                .validateInteger("age", IntegerRule.lengthEqual(8), "age长度必须等于8")
                .result();
        if (result.isFailure()) {
            return result;
        }
        //substep 自定义校验参数（弥补Validator组件的不足）
        if (dto.getAsString("title").contains("@")) {
            return Result.failure().message("title不能包含@符号");
        }

        //step 执行业务
        try {
//            userService.process(dto.getAsString("title"), dto.getAsInt("age"));
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
