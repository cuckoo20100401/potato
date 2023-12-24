package org.potato.util;

public class Test_Result {

    public Result save() {
        Result result = null;
        result = Result.success();
        result = Result.failure();
        result = Result.failure().code(-1);
        result = Result.failure().code(-1).message("名称不能为空");
        result = Result.success().addPayload("books", null);
        result = Result.success().addPayload("books", null).addPayload("author", null);
        return result;
    }

    public void test() {

        Result result = this.save();

        if (result.isSuccess()) {
            Object author = result.getPayload("author");
            System.out.println("执行成功：" + author);
        } else {
            if (result.code() == -1) {
                System.out.println("名称不能为空");
            }
        }

        if (result.isFailure()) {
            System.out.println("执行失败：" + result.message());
        }
    }
}
