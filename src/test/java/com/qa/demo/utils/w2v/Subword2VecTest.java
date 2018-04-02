package com.qa.demo.utils.w2v;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class Subword2VecTest {

    @Test
    void calVec() throws IOException {
        Result res = Subword2Vec.check_words("草豹猪猪");
        System.out.println(Subword2Vec.calVec(res));
    }

    @Test
    void check_words() throws IOException {
        System.out.println(Subword2Vec.check_words("草豹"));
    }
}