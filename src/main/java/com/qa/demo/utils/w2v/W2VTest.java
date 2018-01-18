package com.qa.demo.utils.w2v;

import org.bytedeco.javacpp.Loader;
import org.nd4j.nativeblas.Nd4jCpu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import static com.qa.demo.conf.FileConfig.W2V_file;

public class W2VTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        try {
            Loader.load(Nd4jCpu.class);
        } catch (UnsatisfiedLinkError e) {
            String path = Loader.cacheResource(Nd4jCpu.class, "windows-x86_64/jniNd4jCpu.dll").getPath();
            new ProcessBuilder(W2V_file, path).start().waitFor();
        }

        Word2VecGensimModel w2vModel = null;
        try {
            w2vModel = Word2VecGensimModel.getInstance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            System.out.println("请输入问题，换行表示输入下一题，‘#’结束：");
            String word = scanner.next();
            if (word == "" || word == null || word.equals("#")){
                break;
            }
            double[] vector = w2vModel.getWordVector(word);
            for(Double d : vector)
                System.out.println(d);
        }
    }

}
