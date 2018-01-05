package com.qa.demo.query;

import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.Question;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class QueryPOSKBQATest {
    @Test
    void kbQueryAnswers() {

        Question question=new Question();
        // String string="花生什么时候种植？"; //时候，种植  KG中没有种植属性

        //String string="PigWIN的operatingSystem是什么";
        //String string="FreeRice的screenshot";

        //String string="辐射龟的种是什么";
        //String string="日本蜜蜂属于什么界";  //属于 v 界 k   界无法改变词性

        //String string="油菜[植物]的拉丁学名";
        //String string="朱之文(农民、歌手)的中文名字是什么";
        //String string="茴香(调料：小茴香)的花语是什么";
        //String string="万年青有什么其他名称";
        //String string="延龄草有什么其他名称";

        //String string="花生原产于什么地方？";  //原产 n 地方 n
        //String string="花生主要分布于什么区域？"; // 主要 b 分布 v 区域 n
        //String string="哪里分布有花生"; //哪里 r 分布 v
        //String string="花生产于哪？";  //产 v 哪 r
        //String string="花生产于哪里？"; //产 v 哪里 r
        String string="花生产于哪儿？"; //产 v 哪里 r
        //String string="吴耕民是在哪里逝世的"; //哪里 r 逝世 v KG中没有逝世地点
        //String string="吴耕民是在哪里出生的"; //哪里 r 出生 v
        //String string="孟加拉国猫的分布区域是哪里";
        //String string="黄曲条跳甲主要分布在哪";



        //String string="崔涤僧生于哪天"; //生于 v 哪天 n
        //String string="崔涤僧出生于哪年"; //出生 v 哪年 n
        // String string="崔涤僧逝于什么时候"; //逝 vg 时候 n
        // String string="崔涤僧逝于什么年代"; //哪里 r 年代 n
        //String string="崔涤僧去世于哪年"; //去世 v 哪年 n
        //String string="崔涤僧逝于哪个时代";  //逝 vg 时代 n  （答案差强人意）
        //String string="崔涤僧处于哪个年代";  //处于 v 年代 n
        //String string="何时修剪猕猴桃合适";  //修剪 vn 合适 a  KG无答案
        //String string="猕猴桃修剪时间";  //修剪 vn 时间 n     KG无答案
        //String string="崔涤僧是什么时候出生的";  //时候 n 出生 v

        //String string="谁发现了日本蜜蜂";  //谁 r 发现 v       Radoszkowski, 1877
        //String string="日本蜜蜂由谁发现命名了";  //谁 r 发现 v 命名 v
        //String string="日本蜜蜂被谁发现命名";  //谁 r 发现 v 命名 v
        //String string="日本蜜蜂是谁命名的";  //谁 r 命名 v   Radoszkowski, 1877
        //String string="谁写了2010年执业兽医资格考试应试指南";

        //String string="江口县占地多少？";  //占地 v 多少 r
        //String string="江口县有多少面积";    //多少 r 面积 n
        //String string="江口县有多大面积";    //面积 n
        //String string="2010年执业兽医资格考试应试指南定价多少钱";  //多少 r 钱 n     KG无答案 因为模板中没有该种问法
        //String string="_新希望集团有限公司有几个员工";  //几个 m 员工 n

        //String string="镜面草用二名法怎么命名";  //二名法 n 命名 v
        //String string="用二名法怎么命名镜面草";  //二名法 n 命名 v
        //String string="葡萄树怎么进行冬剪";  //冬剪 v    KG无答案
        //String string="葡萄树的冬剪技术";  //冬剪 v 技术 n   KG无答案
        //String string="怎么预防虫害";  //预防 v
        //String string="怎么对草莓施肥";  //施肥v （这里“施肥”与“镜面草”都是entity）   KG无答案  返回是保存方法
        //String string="怎么用二名法命名镜面草";  //二名法 n 命名 v
        //String string="菜花在定植及定植后如何施肥？";  //镜面 n 草 n （这里“施肥”与“镜面草”都是entity） 返回是栽培技术
        //String string="如何治疗虫害";  //治疗 v
        //String string="如何用二名法命名镜面草";  //二名法 n 命名 v
        //String string="有什么途径传播草莓黄萎病";  //途径 n 传播 vn
        //String string="有哪些方法预防草莓黄萎病";  //方法 n 预防 v
        //String string="有哪些措施预防草莓黄萎病";  //措施 n 预防 v
        //String string="有什么方法预防草莓黄萎病";  //方法 n 预防 v
        //String string="有什么措施预防草莓黄萎病";  //措施 n 预防 v
        //String string="采取什么方法能施肥大白菜";  //方法 n 施 v 白菜 n 这里（把大白菜、采、肥大、施肥均有可能识别entity）返回是栽培技术

        //String string="圆叶西番莲的药用部位是哪儿";
        //String string="稻曲病的为害部位是哪?";
        //String string="豌豆芽枯病的危害部位是哪里";
        //String string="胡萝卜黑斑病的危害部位是哪里";
        //String string="葡萄炭疽病的为害部位是哪里";
        //String string="葡萄褐斑病危害了哪些作物？";  //危害 v 作物 n (草莓黄萎病、作物均会被识别为entity)
        //String string="哪些部位被草莓黄萎病危害";  //部位 n 危害 v
        //String string="草莓黄萎病危害了哪些部位";  //危害 v 部位 n
        //String string="在哪些部位对圆叶西番莲用药？";  //部位 n 圆叶西番莲 n  (药、圆叶西番莲均会被识别为entity)
        //String string="王绶有哪些成就";  //成就 n
        //String string="有什么预防草莓黄萎病的方法";  //预防 v 方法 n
        //String string="有哪些预防草莓黄萎病的方法";  //预防 v 方法 n
        //String string="没药有什么功用";  //功用 n

        //String string="大白菜的施肥技术要点是什么？"; //方法 n 施 v 白菜 n 这里（把大白菜、采、肥大、施肥均有可能识别entity） KG无答案 返回的是栽培方法的答案
        // String string="对大白菜做一下简单描述";  //做 v 简单 a 描述 v
        // String string="保存大白菜的方法是什么";  //保存 v 方法 n       KG无答案 返回的是栽培方法的答案
        //String string="说一下大白菜是什么东西吧";  //说 v 东西 n
        //String string="日本蜜蜂是什么亚纲";  //亚纲 nr
        //String string="王绶是哪个民族的";  //民族 n
        //String string="日本蜜蜂属于哪个亚组";  //属于 v 亚组 n

        //String string="介绍一下日本蜜蜂是什么";  //介绍 v
        //String string="草莓黄萎病主要危害了什么作物";  //主要 b 危害 v 作物 n  (草莓黄萎病、作物均会被识别为entity)


        question.setQuestionString(string);

        KbqaQueryDriver QueryPOSKBQADriver = new QueryPOSKBQA();
        question = QueryPOSKBQADriver.kbQueryAnswers(question);

        System.out.println("Query: "+string);
        question.printQuestionToken();

        System.out.print("The intention of query: ");
        System.out.println(question.getQuestionIntention());

        List<Entity> questionEntity =question.getQuestionEntity();
        for(Entity entity:questionEntity) {
            List<Map<String, String>> entityPos = question.getQuestionEntityPOS().get(entity);
            //输入2个名字entity，但可能来源不同 互动 或者百度百科
            System.out.println("The segment and POS of entity is : "+ entity.getEntityURI());
            for (Map<String, String> b : entityPos)
            {
                for (String token : b.keySet())
                {
                    System.out.print(token + " " + b.get(token) + " ");
                }
            }
            System.out.println();
        }
        List<Answer> answers= question.getCandidateAnswer();
        System.out.println("The answer is :");
        for(Answer ans: answers)
        {
            System.out.println(ans.getAnswerString());
        }
    }
}