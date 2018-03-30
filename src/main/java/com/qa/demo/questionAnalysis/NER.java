package com.qa.demo.questionAnalysis;

import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.Triplet;
import com.qa.demo.utils.kgprocess.KGTripletsClient;
// import javafx.util.Pair;
// import javafx.util.Pair;

import java.util.*;

/**
 *  Created time: 2017_09_08
 *  Author: Devin Hua
 *  Function description:
 *  The method to get entities in one question.
 */
public class NER {
	// 备份
    public static ArrayList<Entity> getEntities_backup(String questionString) {
        HashMap<String, HashSet<String>> candidateEntities
                = new HashMap<>();
        ArrayList<Entity> eList = new ArrayList<>();
        ArrayList<Triplet> triplets = KGTripletsClient.getInstance().getKgTriplets();
//        String questionStringcandidate=questionString.replace("[","(").replace("]",")");
        for (Triplet t : triplets) {
            String sname = t.getSubjectName();
            if(questionString.contains("[")&&questionString.contains("]")) //知识库中只包含"( )"的形式
                questionString=questionString.replace("[","(").replace("]",")");
            if (questionString.contains(sname)) {
                // System.out.println();
                t.printTriplet();
                System.out.println();
                if (candidateEntities.isEmpty() ||
                        !candidateEntities.containsKey(sname)) {
                    HashSet<String> uris = new HashSet<>();
                    uris.add(t.getSubjectURI());
                    candidateEntities.put(sname, uris);
                } else if (candidateEntities.containsKey(sname)) {
                    HashSet<String> uris = candidateEntities.get(sname);
                    uris.add(t.getSubjectURI());
                    candidateEntities.put(sname, uris);
                }
            }
        }

        if(candidateEntities.isEmpty())
            return eList;

        //例如草莓属于什么属？可以连接到草莓属和草莓，应该将草莓属删掉；
        if(questionString.contains("属于")&&!questionString.contains("属属"))
        {
            Set tempSet = candidateEntities.keySet();
            Iterator it1 = tempSet.iterator();
            ArrayList<String> keys = new ArrayList<>();
            while (it1.hasNext()) {
                String key = it1.next().toString();
                if(key.contains("属"))
                    keys.add(key);
            }
            if(!keys.isEmpty())
            {
                for(String key : keys)
                {
                    candidateEntities.remove(key);
                }
            }
        }

        if(candidateEntities.isEmpty())
            return eList;

        //若某个entity名包含于另一个entity名，则将其从候选中删掉；
        Set tempSet = candidateEntities.keySet();
        Iterator it1 = tempSet.iterator();
        ArrayList<String> keys = new ArrayList<String>();
        while (it1.hasNext()) {
            keys.add((String)it1.next());
        }

        for (String key : keys) {
            boolean deleteFlag = false; //若要将此key删掉，则置true;
            Iterator it2 = candidateEntities.keySet().iterator();
            while (it2.hasNext()) {
                String temp = (String)it2.next();
                if (temp.contains(key)&&!temp.equalsIgnoreCase(key)) {
                    deleteFlag = true;
                    break;
                }
            }
            if (deleteFlag)
                candidateEntities.remove(key);
        }

        if (!candidateEntities.isEmpty() || candidateEntities != null) {
            Iterator it = candidateEntities.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, HashSet<String>> entry =
                        (Map.Entry<String, HashSet<String>>) it.next();
                for (String temp : entry.getValue()) {
                    Entity e = new Entity();
                    e.setEntityURI(temp);
                    e.setKgEntityName(entry.getKey());
                    eList.add(e);
                }
            }
        }
        return eList;
    }

    // 被动实体链接器
    public static ArrayList<Entity> getEntities(String questionString) {

        HashMap<String, Pair<Double, HashSet<String>>> candidateEntities
                = new HashMap<>();
        ArrayList<Entity> entityList = new ArrayList<>();
        ArrayList<Triplet> tripletList = KGTripletsClient.getInstance().getKgTriplets();

        double alpha = 0.6, beita = 0.6;


        if(questionString.contains("[")&&questionString.contains("]")) //知识库中只包含"( )"的形式
            questionString=questionString.replace("[","(").replace("]",")");

        // 调节参数，中文情况下实体一般是出现在前面，有些疑问句会使实体出现在后面
        // alpha + beita > 1 时，实体出现在前面的可能性高
        //               < 1 时，实体出现在后面的可能性高
        // 怎么对...进行栽培? 实体出现在问题中间（前面）
        if(questionString.substring(0, 3).equals("怎么对") || questionString.substring(0, 3).equals("如何对")){
            alpha = 0.6;
            beita = 0.6;
        } // 怎么栽培柚? 对于这种问句实体应存在于后边
        else if(questionString.substring(0, 2).equals("怎么") || questionString.substring(0, 2).equals("如何")){
            alpha = 0.3;
            beita = 0.3;
        } // 哪些...被...
        else if(questionString.contains("哪些") && questionString.contains("被")){
            alpha = 0.3;
            beita = 0.3;
        }


        char[] questionCharArray = questionString.toCharArray();

        for (Triplet t : tripletList) {
            String sname = t.getSubjectName();

            if (_isCandidate(questionCharArray, sname)) {
                Pair<String, Double> ret = _passiveEntityLinker(questionString,
                        t.getSubjectName(), alpha, beita);

                if (candidateEntities.isEmpty() ||
                        !candidateEntities.containsKey(sname)) {
                    HashSet<String> uris = new HashSet<>();
                    uris.add(t.getSubjectURI());
                    candidateEntities.put(sname, new Pair<>(ret.getValue(), uris));
                } else if (candidateEntities.containsKey(sname)) {
                    HashSet<String> uris = candidateEntities.get(sname).getValue();
                    uris.add(t.getSubjectURI());
                    // candidateEntities.put(sname, uris);
                }
            }
        }

        if(candidateEntities.isEmpty())
            return entityList;

        // 例如草莓属于什么属？可以连接到草莓属和草莓，应该将草莓属删掉；
        // 有些实体里面包含有属这一个字, 需要根据其是否出现在最后一位来进行区分. 比如: 草莓属 是要被剔除的, 而 小麦(...属..) 是要被保留的
        // 哦，这里也可以加入到下面的循环里面，才注意到
        if(questionString.contains("属于")&&!questionString.contains("属属"))
        {
            Set tempSet = candidateEntities.keySet();
            Iterator it1 = tempSet.iterator();
            ArrayList<String> keys = new ArrayList<>();
            while (it1.hasNext()) {
                String key = it1.next().toString();
                if(key.substring(key.length() - 1).equals("属"))
                    keys.add(key);
            }
            if(!keys.isEmpty())
            {
                for(String key : keys)
                {
                    candidateEntities.remove(key);
                }
            }
        }

        if(candidateEntities.isEmpty())
            return entityList;

        int index;
        Iterator it = candidateEntities.entrySet().iterator();
        List<Double> scoreList = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<String, Pair<Double, HashSet<String>>> entry =
                    (Map.Entry<String, Pair<Double, HashSet<String>>>) it.next();

            String first = entry.getKey().substring(0, 1);
            try{
                if("栽".contains(first) && questionString.contains("栽培")){
                    index = questionString.indexOf("栽培");
                    if(index != 0){

                        // 如果前面是 了
                        if(questionString.substring(index - 1, index).equals("了"))
                            ;
                        // 如果前面是 怎么
                        if(questionString.substring(index - 2, index).equals("怎么"))
                            continue;

                        if(questionString.contains("的"))
                            continue;
                    }

                }
                else if("施".contains(first) && questionString.contains("施肥") && !questionString.substring(questionString.indexOf("施肥") - 1, questionString.indexOf("施肥")).equals("对"))
                    continue;
                else if("下".contains(first) && questionString.contains("一下") && !questionString.contains("下下") && !questionString.substring(questionString.indexOf("下") - 1, questionString.indexOf("下")).equals("对"))
                    continue;
                else if("对".contains(first) && questionString.contains("做"))
                    continue;
                else if("肥".contains(first) && questionString.contains("如何对"))
                    continue;
                else if("有".contains(first) && (questionString.contains("哪里有") || questionString.contains("哪里分布有")) && !questionString.contains("有有"))
                    continue;
                else if("采".contains(first) && questionString.contains("采取"))
                    continue;
                else if("藏".contains(first) && (questionString.contains("储藏") || questionString.contains("保藏")))
                    continue;
                else if("养".contains(first) && questionString.contains("如何"))
                    continue;
            }catch (IndexOutOfBoundsException e){
                continue;
            }
            for (String temp : entry.getValue().getValue()) {
                Entity e = new Entity();
                e.setEntityURI(temp);
                e.setKgEntityName(entry.getKey());
                _insert(scoreList, entityList, entry.getValue().getKey(), e);
            }
        }
        return entityList;
    }

    /**
     * 判断实体名字和问题是否有交集
     *
     * @param charArray question
     * @param entity entity
     * @return 是否有交集
     */
    private static boolean _isCandidate(char[] charArray, String entity){
        for (char ch : charArray){
            if (entity.indexOf(ch) != -1)
                return true;
        }
        return false;
    }

    /**
     * 将score插入到scoreList, entity插入到entityList, 插入时按照score递减排序
     *
     * @param scoreList scoreList
     * @param entityList entityList
     * @param score score
     * @param entity entity
     */
    private static void _insert(List<Double> scoreList, List<Entity> entityList, Double score, Entity entity){
        if(scoreList.size() == 0){
            scoreList.add(score);
            entityList.add(entity);
        }else {
            if(score - scoreList.get(0) > 0.00001){
                scoreList.clear();
                entityList.clear();
                scoreList.add(score);
                entityList.add(entity);
            }
            else if (score - scoreList.get(0) < -0.00001) {

            }else{
                for(int i = 0; i < entityList.size(); ++i){
                    if(entity.getKgEntityName().contains(entityList.get(i).getKgEntityName())){
                        entityList.add(i, entity);
                        scoreList.add(i, score);
                        return;
                    }
                }
                scoreList.add(score);
                entityList.add(entity);
            }
        }
    }

    /**
     * 被动实体链接器
     * @param question 原问题
     * @param entity 候选实体
     * @param alpha 超参数 alpha
     * @param beita 超参数 beita
     * @return <ret, score> ret 为抽取结果，score 为得分
     */
    private static Pair<String, Double> _passiveEntityLinker(String question, String entity, double alpha, double beita){

        // 寻找 question 和 entity 的最长公共子串
        String strA = "#" + question;
        String strB = "#" + entity;
        int n = question.length(), m = entity.length();
        int[][] C = new int[n+1][m+1];
        for(int i=0; i <= n; i++)
            C[i][0] = 0;
        for(int i=0; i <= m; i++)
            C[0][i] = 0;
        int maxL = -1, x = 0, y = 0;
        for(int i=1; i <= n; i++) {
            for(int j=1; j <= m; j++) {
                if(strA.charAt(i) == strB.charAt(j))
                    C[i][j] = C[i-1][j-1] + 1;
                else
                    C[i][j] = 0;

                if(C[i][j] > maxL) {
                    maxL = C[i][j];
                    x = i;
                    y = j;
                }
            }
        }

        // 计算各参数，ret 为抽取结果
        String intersect = entity.substring(y - C[x][y], y);
        int lastOccurIndex = x - 1;
        double a = (double)intersect.length() / question.length();
        double b = (double)intersect.length() / entity.length();
        double c = (double)lastOccurIndex / question.length();
        double score = alpha * a + beita * b + (1 - alpha - beita) * c;
        String ret;
        if(entity.length() == C[x][y]){
            ret = intersect;
        }else{
            int gap = entity.length() - C[x][y];
            if(x - C[x][y] - gap >= 0){
                // 向左扩展
                ret = question.substring(x - C[x][y] - gap, x - C[x][y]) + intersect;
            }else if(x + gap < question.length()){
                // 向右扩展
                ret = intersect + question.substring(x, x + gap);
            }else{
                ret = intersect;
            }
        }

        return new Pair<>(ret, score);
    }


    private static class Pair<K, V> {
        private K key;
        private V value;
        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        K getKey(){
            return key;
        }
        V getValue(){
            return value;
        }
    }
}
