package com.qa.demo.utils.es;

import com.qa.demo.conf.Configuration;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class DeleteIndexTest {

    /**
     * 删除索引
     * @throws UnknownHostException
     */
    @Test
    void deleteIndex() throws UnknownHostException {
        DeleteIndex.deleteIndex(Configuration.ES_INDEX_FAQ);
    }

}