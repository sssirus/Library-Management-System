package com.qa.demo.utils.qgeneration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyCountTest {
    @Test
    void count() {
    }

    @Test
    void writeCountsToFile() {

        PropertyCount.writeCountsToFile(PropertyCount.count());

    }

}