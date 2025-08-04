package org.potato.security.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.potato.security.handler.LogHandler;

import java.util.Map;

/**
 * DefaultLogHandler
 */
public class DefaultLogHandler implements LogHandler {

    private static final Logger logger = LogManager.getLogger(DefaultLogHandler.class);

    private ObjectMapper objectMapper;

    public DefaultLogHandler() {
        objectMapper = new ObjectMapper();
    }

    /**
     * 日志触发的回调函数
     *
     * @param log
     */
    @Override
    public void onLog(Map<String, String> log) {
        try {
            logger.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(log));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
