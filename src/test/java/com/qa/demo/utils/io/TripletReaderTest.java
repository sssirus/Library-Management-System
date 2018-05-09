package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TripletReaderTest {

    @Test
    void analysys_zhwiki_abstracts() throws IOException {
        // String object =URLDecoder.decode("\"", "UTF-8");
        // List<Triplet> tripletList = TripletReader.analysys_zhwiki_abstracts("src/main/resources/data/kbfile/zhwiki_infobox.nt");
        // TripletWriter.write_triplets_to_file(tripletList, false, "src/main/resources/data/kbfile/zhwiki_infobox_decode.nt");
        // List<Triplet> tripletList = TripletReader.analysys_zhwiki_abstracts("src/main/resources/data/kbfile/zhwiki_infobox.nt");

        // String str = "\u4E2D\u56FD\u94C1\u8DEF\u8D27\u8F66\u57FA\u672C\u578B\u53F7\u7528\u201CC\u201D\u8868\u793A\u655E\u8F66\uFF0C\u662F\u6C49\u8BED\u62FC\u97F3\u201CChang Che\u201D\u7684\u5934\u4E00\u4E2A\u58F0\u6BCD\u7684\u7B80\u5199\u3002\u4E2D\u56FD\u8D27\u8F66\u603B\u6570\u4E2D\uFF0C\u655E\u8F66\u6570\u91CF\u6700\u591A\uFF0C\u7EA6\u536060%\u3002";
        /*List<Triplet> tripletList = TripletReader.getTripletsFromNT_Triplets("src/main/resources/data/kbfile/zhwiki_infobox_decode.nt");
        int max = tripletList.size();
        // int max = 333333;
        Random rnd = new Random();
        int[] nums = new int[200];
        int p = rnd.nextInt(max);
        for(int i = 0; i < nums.length; ++i){
            nums[i] = rnd.nextInt(max);
            Triplet triplet = tripletList.get(nums[i]);
            System.out.println(triplet.getSubjectURI() + ' ' + triplet.getPredicateURI() + ' ' + triplet.getObjectURI());
        }*/
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File("src/main/resources/data/kbfile/zhwiki_infobox_decode.nt"))));
        String str;

        List<Triplet> tripletList = new ArrayList<>();
        String subject, predict, object;
        while ((str = bufferedReader.readLine()) != null) {
            try {
                char[] chars = str.toCharArray();
                int left = 0, right;

                while (chars[left] != '<') ++left;
                right = left + 1;
                while (chars[right] != '>') ++right;
                subject = str.substring(left, right + 1);

                left = right + 1;
                while (chars[left] != '<') ++left;
                right = left + 1;
                while (chars[right] != '>') ++right;
                predict = str.substring(left, right + 1);

                left = right + 1;
                while (chars[left] != '\"') ++left;
                right = left + 1;
                while (chars[right] != '\"') ++right;
                object = str.substring(left, right + 1);
            } catch (Exception e) {
                System.out.println(123);
            }

        }
    }
}