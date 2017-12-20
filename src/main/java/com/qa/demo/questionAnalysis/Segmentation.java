package com.qa.demo.questionAnalysis;

/**
 * Created by Devin Hua on 2017/10/06.
 * Function description:
 * To tokenize the sentence and get relevant POS.
 */

import com.qa.demo.conf.FileConfig;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;

import java.util.*;

public class Segmentation {

    private static List<String> tokens;
    private static List<Map<String, String>> tokenPOSList;

    public static List<Map<String, String>> getTokenPOSList() {
        return tokenPOSList;
    }

    public static void setTokenPOSList(List<Map<String, String>> tokenPOSList) {
        Segmentation.tokenPOSList = tokenPOSList;
    }

    public static List<String> getTokens() {
        return tokens;
    }

    public static void setTokens(ArrayList<String> tokens) {
        Segmentation.tokens = tokens;
    }

    //输入一个字符串，对其进行分词后输出；
    public static void segmentation(String sentence) {

        tokens = new ArrayList<>();
        tokenPOSList = new ArrayList<>();
        Forest forest = null;
        //官方预设的自定分词词典;
        try {
            forest = Library.makeForest(FileConfig.DICTIONARY_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Forest forest1 = new Forest();
        //为分词词典增加停用词，最后一个参数的分值越大， 越按照这个词来分词；
        //比如说“可以”的词频为1000，“都可以”的词频为1001，那么都可以的三个字就不会分开；
        //第二个参数指词性；
        Library.insertWord(forest1, new Value("开本", "n", "1000"));
        Library.insertWord(forest1, new Value("茶类", "n", "1000"));
        Library.insertWord(forest1, new Value("闪点", "n", "1000"));
        Library.insertWord(forest1, new Value("接手人", "n", "1000"));
        Library.insertWord(forest1, new Value("投资商", "n", "1000"));
        Library.insertWord(forest1, new Value("固形物", "n", "1000"));
        Library.insertWord(forest1, new Value("最高职务", "n", "1000"));
        Library.insertWord(forest1, new Value("亚科", "n", "1000"));
        Library.insertWord(forest1, new Value("摩尔质量", "n", "1000"));
        Library.insertWord(forest1, new Value("参战方", "n", "1000"));
        Library.insertWord(forest1, new Value("兴造", "n", "1000"));
        Library.insertWord(forest1, new Value("萃取", "v", "1000"));
        Library.insertWord(forest1, new Value("总科", "n", "1000"));
        Library.insertWord(forest1, new Value("前一节气", "n", "1000"));
        Library.insertWord(forest1, new Value("最大功率", "n", "1000"));
        Library.insertWord(forest1, new Value("亚属", "n", "1000"));
        Library.insertWord(forest1, new Value("性味", "n", "1000"));
        Library.insertWord(forest1, new Value("归经", "n", "1000"));
        Library.insertWord(forest1, new Value("性味与归经", "n", "1000"));
        Library.insertWord(forest1, new Value("尊号", "n", "1000"));
        Library.insertWord(forest1, new Value("舞龄", "n", "1000"));
        Library.insertWord(forest1, new Value("郑码", "n", "1000"));
        Library.insertWord(forest1, new Value("族拉丁名", "n", "1000"));
        Library.insertWord(forest1, new Value("又名", "n", "1000"));
        Library.insertWord(forest1, new Value("接档", "n", "1000"));
        Library.insertWord(forest1, new Value("属别", "n", "1000"));
        Library.insertWord(forest1, new Value("招术", "n", "1000"));
        Library.insertWord(forest1, new Value("校区", "n", "1000"));
        Library.insertWord(forest1, new Value("改造后名称", "n", "1000"));
        Library.insertWord(forest1, new Value("下限", "n", "1000"));
        Library.insertWord(forest1, new Value("上限", "n", "1000"));
        Library.insertWord(forest1, new Value("高限", "n", "10000"));
        Library.insertWord(forest1, new Value("参展方", "n", "1000"));
        Library.insertWord(forest1, new Value("命名人", "n", "1000"));
        Library.insertWord(forest1, new Value("偶极矩", "n", "1000"));
        Library.insertWord(forest1, new Value("郡望", "n", "1000"));
        Library.insertWord(forest1, new Value("市值", "n", "1000"));
        Library.insertWord(forest1, new Value("亚族", "n", "1000"));
        Library.insertWord(forest1, new Value("亚门", "n", "1000"));
        Library.insertWord(forest1, new Value("现居地", "n", "1000"));
        Library.insertWord(forest1, new Value("古称", "n", "1000"));
        Library.insertWord(forest1, new Value("逝世地", "n", "1000"));
        Library.insertWord(forest1, new Value("横径", "n", "1000"));
        Library.insertWord(forest1, new Value("其他名", "n", "1000"));
        Library.insertWord(forest1, new Value("现在居住地", "n", "1000"));
        Library.insertWord(forest1, new Value("果期", "n", "1000"));
        Library.insertWord(forest1, new Value("性味归经", "n", "1000"));
        Library.insertWord(forest1, new Value("等级", "n", "1000"));
        Library.insertWord(forest1, new Value("发行商", "n", "1000"));
        Library.insertWord(forest1, new Value("博士点", "n", "1000"));
        Library.insertWord(forest1, new Value("亚纲", "n", "1000"));
        Library.insertWord(forest1, new Value("关联犬种", "n", "1000"));
        Library.insertWord(forest1, new Value("国际电话区号", "n", "1000"));
        Library.insertWord(forest1, new Value("亚组", "n", "1000"));
        Library.insertWord(forest1, new Value("是否管制", "n", "1000"));
        Library.insertWord(forest1, new Value("入侵地", "n", "1000"));
        Library.insertWord(forest1, new Value("亚目", "n", "1000"));
        Library.insertWord(forest1, new Value("外文名", "n", "1000"));
        Library.insertWord(forest1, new Value("下辖地区", "n", "1000"));
        Library.insertWord(forest1, new Value("基尼系数", "n", "1000"));
        Library.insertWord(forest1, new Value("运营商", "n", "1000"));
        Library.insertWord(forest1, new Value("训练家", "n", "1000"));
        Library.insertWord(forest1, new Value("开园", "v", "1000"));
        Library.insertWord(forest1, new Value("闭园", "v", "1000"));
        Library.insertWord(forest1, new Value("说文解字", "v", "1000"));
        Library.insertWord(forest1, new Value("片长", "n", "1000"));
        Library.insertWord(forest1, new Value("近义词", "n", "1000"));
        Library.insertWord(forest1, new Value("仓颉", "n", "1000"));
        Library.insertWord(forest1, new Value("基本组成", "n", "1000"));
        Library.insertWord(forest1, new Value("所在洲", "n", "1000"));
        Library.insertWord(forest1, new Value("注意事项", "n", "1000"));
        Library.insertWord(forest1, new Value("名人", "n", "1000"));
        Library.insertWord(forest1, new Value("饲育适温", "n", "1000"));
        Library.insertWord(forest1, new Value("原产国", "n", "1000"));
        Library.insertWord(forest1, new Value("谥号", "n", "1000"));
        Library.insertWord(forest1, new Value("花果期", "n", "1000"));
        Library.insertWord(forest1, new Value("不良反应", "n", "1000"));
        Library.insertWord(forest1, new Value("菜系", "n", "1000"));
        Library.insertWord(forest1, new Value("年盈利", "n", "1000"));
        Library.insertWord(forest1, new Value("译书", "n", "1000"));
        Library.insertWord(forest1, new Value("出品人", "n", "1000"));
        Library.insertWord(forest1, new Value("发现者", "n", "1000"));
        Library.insertWord(forest1, new Value("二名法", "n", "1000"));
        Library.insertWord(forest1, new Value("园主", "n", "1000"));
        Library.insertWord(forest1, new Value("记录号", "n", "1000"));
        Library.insertWord(forest1, new Value("学历", "n", "1000"));
        Library.insertWord(forest1, new Value("年降水量", "n", "1000"));
        Library.insertWord(forest1, new Value("毛长", "n", "1000"));
        Library.insertWord(forest1, new Value("别名", "n", "1000"));
        Library.insertWord(forest1, new Value("别称", "n", "1000"));
        Library.insertWord(forest1, new Value("易感人群", "n", "1000"));
        Library.insertWord(forest1, new Value("属于", "v", "1000"));
        Library.insertWord(forest1, new Value("原作者", "n", "1000"));
        Library.insertWord(forest1, new Value("产期", "n", "1000"));
        Library.insertWord(forest1, new Value("命名者及时间", "n", "1000"));
        Library.insertWord(forest1, new Value("年营业额", "n", "1000"));
        Library.insertWord(forest1, new Value("晋祠三绝", "n", "1000"));
        Library.insertWord(forest1, new Value("三绝", "n", "1000"));
        Library.insertWord(forest1, new Value("相对分子质量", "n", "1000"));
        Library.insertWord(forest1, new Value("长江学者", "n", "1000"));
        Library.insertWord(forest1, new Value("硕士点", "n", "1000"));
        Library.insertWord(forest1, new Value("村里数", "n", "1000"));
        Library.insertWord(forest1, new Value("营收", "n", "1000"));
        Library.insertWord(forest1, new Value("经营范围", "n", "1000"));
        Library.insertWord(forest1, new Value("外号", "n", "1000"));
        Library.insertWord(forest1, new Value("官方网站", "n", "1000"));
        Library.insertWord(forest1, new Value("个体高大", "n", "1000"));
        Library.insertWord(forest1, new Value("纲拉丁名", "n", "1000"));
        Library.insertWord(forest1, new Value("游戏特点", "n", "1000"));
        Library.insertWord(forest1, new Value("友好城市", "n", "1000"));
        Library.insertWord(forest1, new Value("同物异名", "n", "1000"));
        Library.insertWord(forest1, new Value("四角号码", "n", "1000"));
        Library.insertWord(forest1, new Value("本店出现剧集", "n", "1000"));
        Library.insertWord(forest1, new Value("总资产", "n", "1000"));
        Library.insertWord(forest1, new Value("源产地", "n", "1000"));
        Library.insertWord(forest1, new Value("石门荣誉", "n", "1000"));
        Library.insertWord(forest1, new Value("犬种分类", "n", "1000"));
        Library.insertWord(forest1, new Value("命名者", "n", "1000"));
        Library.insertWord(forest1, new Value("门拉丁名", "n", "1000"));
        Library.insertWord(forest1, new Value("后一节气", "n", "1000"));
        Library.insertWord(forest1, new Value("政治面貌", "n", "1000"));
        Library.insertWord(forest1, new Value("亚科", "n", "1000"));
        Library.insertWord(forest1, new Value("亚种", "n", "1000"));
        Library.insertWord(forest1, new Value("参战方兵力", "n", "1000"));
        Library.insertWord(forest1, new Value("身后哀荣", "n", "1000"));
        Library.insertWord(forest1, new Value("地点", "n", "1000"));
        Library.insertWord(forest1, new Value("附赠商品", "n", "1000"));
        Library.insertWord(forest1, new Value("食材", "n", "1000"));
        Library.insertWord(forest1, new Value("完成时间", "n", "1000"));
        Library.insertWord(forest1, new Value("原产", "n", "1000"));
        Library.insertWord(forest1, new Value("中文学名", "n", "1000"));
        Library.insertWord(forest1, new Value("安全术语", "n", "1000"));
        Library.insertWord(forest1, new Value("五笔", "n", "1000"));
        Library.insertWord(forest1, new Value("病名", "n", "1000"));
        Library.insertWord(forest1, new Value("体长", "n", "1000"));
        Library.insertWord(forest1, new Value("制作成本", "n", "1000"));
        Library.insertWord(forest1, new Value("水域率", "n", "1000"));
        Library.insertWord(forest1, new Value("毛型", "n", "1000"));
        Library.insertWord(forest1, new Value("新浪微博", "n", "1000"));
        Library.insertWord(forest1, new Value("花语", "n", "1000"));
        Library.insertWord(forest1, new Value("通用名", "n", "1000"));
        Library.insertWord(forest1, new Value("肩高", "n", "1000"));
        Library.insertWord(forest1, new Value("纵径", "n", "1000"));
        Library.insertWord(forest1, new Value("谥曰", "n", "1000"));
        Library.insertWord(forest1, new Value("侵略者帮凶", "n", "1000"));
        Library.insertWord(forest1, new Value("叫什么", "v", "1000"));
        Library.insertWord(forest1, new Value("海拨低限", "n", "1000"));
        Library.insertWord(forest1, new Value("扭矩", "n", "1000"));
        Library.insertWord(forest1, new Value("命名人及年代", "n", "1000"));
        Library.insertWord(forest1, new Value("大小", "n", "1000"));
        Library.insertWord(forest1, new Value("拼音名", "n", "1000"));
        Library.insertWord(forest1, new Value("区号", "n", "1000"));
        Library.insertWord(forest1, new Value("哪天", "n", "1000"));
        Library.insertWord(forest1, new Value("哪年", "n", "1000"));

//        Result terms = ToAnalysis.parse(sentence, forest1);
//        Result terms = ToAnalysis.parse(sentence, forest);
        Result terms = ToAnalysis.parse(sentence, forest, forest1);
//        System.out.println(terms);

        HashSet<String> stopwords = MoveStopwords.getInstance().getStopwordSet();
        for (int i = 0; i < terms.size(); i++) {
            String s = terms.get(i).toString();
            if (s.contains("/") && (!s.endsWith("/"))) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(s.split("/")[0], s.split("/")[1]);
                String token = s.split("/")[0];
                if (!stopwords.isEmpty()) {
                    if (!stopwords.contains(token)) {
                        tokens.add(token);
                        tokenPOSList.add(map);
                    }
                }
            }
        }
    }
}

