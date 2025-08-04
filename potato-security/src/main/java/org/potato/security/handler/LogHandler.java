package org.potato.security.handler;

import java.util.Map;

/**
 * LogHandler
 *
 * <p>
 *     日志处理器，在该方法中可以打印或保存日志信息
 * </p>
 */
public interface LogHandler {

    /**
     * 日志触发的回调函数
     *
     * @param log
     */
    void onLog(Map<String, String> log);
}