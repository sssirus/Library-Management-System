package com.qa.demo.questionAnalysis;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.TopologicalPattern;
import com.qa.demo.dataStructure.TopologicalStructure;
import com.qa.demo.utils.topologicalpattern.TopologicalPatternClient;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 *  Created time: 2018_01_31
 *  Author: Devin Hua
 *  Function description:
 *  The class to analyze and get topological pattern
 *  from natural language question.
 */

public class TopologicalPatternMatch {

    private static TopologicalPatternMatch uniqueInstance;
    private LexicalizedParser lp;

    public LexicalizedParser getLp() {
        return lp;
    }

    public void setLp(LexicalizedParser lp) {
        this.lp = lp;
    }

    private TopologicalPatternMatch(){
        //初始化用于句法分析的类LexicalizedParser，函数loadModel 加载模型文件。
        this.setLp(LexicalizedParser.loadModel(FileConfig.TP_CNN_MODEL));
    }

    public static synchronized TopologicalPatternMatch getInstance()
    {
        if(uniqueInstance==null)
        {
            uniqueInstance = new TopologicalPatternMatch();
        }
        return uniqueInstance;
    }

    //根据分词之后的tokens，使用Standford Parser得到句法树，并返回代表句法树结构的字符串；
    // The input is a list of correctly tokenized words
    public String getParseTreeString(String[] sentence) {

        //A CoreLabel represents a single word with ancillary information
        // attached using CoreAnnotations.
        List<CoreLabel> rawWords = new ArrayList<>();
        for (String word : sentence) {
            CoreLabel l = new CoreLabel();
            l.setWord(word);
            rawWords.add(l);
        }

        //进行句法分析；
        Tree parse_tree = lp.apply(rawWords);

        ArrayList<String> parse_tree_sentences = new ArrayList<>();
        Iterator it = parse_tree.iterator();
        while(it.hasNext())
            parse_tree_sentences.add(it.next().toString());
        String parse_tree_sentence = parse_tree_sentences.get(1);
        parse_tree_sentence = parse_tree_sentence.replace('(','[');
        parse_tree_sentence = parse_tree_sentence.replace(')',']');

//        //调用Tree.class里的pennPrint方法打印句法分析树；
//        System.out.println("Tree is: ");
//        parse_tree.pennPrint();
//        System.out.println();
//
//        //得到treebankLanguageBank抽象类;
//        //This interface specifies language/treebank specific information for a Treebank,
//        // which a parser or other treebank user might need to know.
//        TreebankLanguagePack tlp = lp.getOp().langpack();
//
//        //进行依存分析；
//        //A TypedDependency is a relation between two words in a GrammaticalStructure.
//        //Each TypedDependency consists of a governor word, a dependent word,
//        // and a relation, which is normally an instance of GrammaticalRelation.
//        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
//        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse_tree);
//        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
//        //打印依存关系；
//        System.out.println("TypedDependency is: ");
//        System.out.println(tdl);
//        System.out.println();
//
//        TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed",tlp);
//        tp.printTree(parse_tree);
        return parse_tree_sentence;
    }

    //依据词性标注确认最终需要保留的词（句法树的叶子结点）；
    //使用Replace方法用占位符替换掉句子中的 疑问词/短语 并记录 非疑问词/短语 在句子中的位置编号；
    //输入是句子的词性标注序列，输出是 非疑问词/短语 在句子中的位置编号的数组序列（保留位置序列）；
    public ArrayList<Integer> extractNodebyPostag(String posSequence)
    {
//        System.out.println(posSequence);
        posSequence = this.replaceParseSentence(posSequence);
//        System.out.println(posSequence);
        String[] posSequenceArray = posSequence.split(" ");
        //记录非疑问词/短语的句子成分，在原pos序列中的下标；
        ArrayList<Integer> nonXIndex = new ArrayList<>();
        for(int i=0;i<posSequenceArray.length;i++)
        {
            if(!posSequenceArray[i].equalsIgnoreCase("X"))
                nonXIndex.add(i+1);
        }

        //记录非疑问词/短语的句子成分序列的最大下标；
        int maxIndex = Integer.MIN_VALUE;
        for(Integer temp : nonXIndex)
            maxIndex = temp>maxIndex ? temp: maxIndex;

        int minIndex = Integer.MAX_VALUE;
        for(Integer temp : nonXIndex)
            minIndex = temp<minIndex ? temp: minIndex;

        //记录非疑问词/短语的句子成分的连续序列；
        for(int i=maxIndex;i>=minIndex;i--)
        {
            if(i>=minIndex&&!nonXIndex.contains(i))
                nonXIndex.add(i);
        }
//        System.out.println(nonXIndex);
        return nonXIndex;
    }

    //使用占位符替换掉标注序列中的 疑问词/短语 标注
    private String replaceParseSentence(String posSequence)
    {
        String pos_sequence = posSequence.replace("uj", "X");
        pos_sequence = pos_sequence.replace("v r", "X X");
        pos_sequence = pos_sequence.replace("p r", "X X");
        pos_sequence = pos_sequence.replace("v m", "X X");
        pos_sequence = pos_sequence.replace("\n", "");
        return pos_sequence;
    }

    //依据问句中需要保留的词，从句法树中抽取出包含这些词对应叶子结点的子树；
    //输入是句子的 句法树序列 和 保留位置序列 ，输出的是句子的子树序列；
    //实验证明该模板还是需要一点人工校对后处理去除某些因分词错误得到的无用模板；
    public String extractSubTree(String posSequence, String[] sentence)
    {
        ArrayList<Integer> nonXIndices = this.extractNodebyPostag(posSequence);
        String parseTreeSentence = getParseTreeString(sentence);
        String subtreeString = "";
        String[] parseTreeSentenceArray = parseTreeSentence.split(" ");
        int count = 0;

        int maxIndex = Integer.MIN_VALUE;
        for(Integer inttemp : nonXIndices)
            maxIndex = inttemp>maxIndex ? inttemp: maxIndex;

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        for(String temp : parseTreeSentenceArray)
        {
            if(temp.contains("ENTITY")||temp.contains("entity")||p.matcher(temp).find())
            {
                count++;
                if(nonXIndices.contains(count))
                {
                    subtreeString += " "+temp;
                }
            }

            else
            {
                if(count < maxIndex)
                    subtreeString += " "+temp;
            }
        }

        //补几个‘]’；
        int num = 0;
        subtreeString = subtreeString.trim();
        char[] subtreeChars = subtreeString.toCharArray();
        for(char temp : subtreeChars)
        {
            if(temp=='[')
                num++;
            if(temp==']')
                num--;
        }
        for(int i=0;i<num;i++)
        {
            subtreeString+="]";
        }
        return subtreeString;
    }

    public ArrayList<ArrayList<String>> buildTopologicalPattern(String subtreeString)
    {
        //由于可能有重复模板，故返回的谓词指称可能不止一个，需要返回一个列表的列表；
        ArrayList<ArrayList<String>> predicateMentionWordList = new ArrayList<>();
        String topologicalPatternString = "";
        int count = 0;//node的表示层数；
        int no = 0;//表示出现'['的元素位置下标；
        ArrayList<Integer> countList = new ArrayList<>();//表示层数的列表；
        ArrayList<Integer> noList = new ArrayList<>();//表示'['元素下标列表；
        char[] subtreeStringArray = subtreeString.toCharArray();//表示子树的字符数组；

        for(char temp : subtreeStringArray)
        {
            if(temp=='[')
            {
                count++;
                countList.add(count);
                noList.add(no);
            }
            else if(temp==']')
            {
                count--;
            }
            no++;
        }

        //一维数组，两个元素，第二个元素表示第一个分岔节点的层数，
        //即层数为k的节点在子树中出现了大于1次，表示这是一个子树节点，也是从上往下找到第一个出现分岔树的节点；
        //第一个元素表示这个子树节点的上一层root的层数；
        int[] savtree = new int[2];
        savtree[0] = countList.get(0);

        //判断出现分支的节点，及出现分岔树的节点，然后取该节点与下一层的子节点构成模板;
        for(int k : countList)
        {
            int c = 0;
            for(int l : countList)
            {
                if(k == l)
                    ++c;
            }
            if(c>1)
            {
                savtree[1] = k;
                break;
            }
            else
                savtree[0] = k;
        }

        //表示组成模板的句子成分;
        ArrayList<String> patternElements = new ArrayList<>();
        //用hashmap记录每个POS出现的次数，若大于一次，则在POS后面加上“1”、“2”、“3”...的记号；
        //使用队列来记录相同POS（如NN1，NN2，NN3）出现的次数；
        HashMap<String, Integer> patternElementCount = new HashMap<>();
        HashMap<String, Queue<Integer>> patternElementCountQueue = new HashMap<>();
        for(int i=0;i<countList.size();i++)
        {
            if(countList.get(i)==savtree[0]||countList.get(i)==savtree[1])
            {
                int index = noList.get(i);
                String elementString = "";
                for(int j=index;j<index+5;j++)
                    elementString+=subtreeStringArray[j];
                elementString = elementString.split("\\[")[1].trim();
                //表示为子树节点；
                if(countList.get(i)==savtree[1])
                {
                    if(!patternElementCount.containsKey(elementString))
                    {
                        patternElementCount.put(elementString,1);
                        Queue<Integer> queue = new LinkedList<>();
                        queue.offer(1);
                        patternElementCountQueue.put(elementString,queue);
                    }
                    else
                    {
                        int posCount = patternElementCount.get(elementString) + 1;
                        patternElementCount.put(elementString,posCount);
                        Queue<Integer> queue = patternElementCountQueue.get(elementString);
                        queue.offer(posCount);
                        patternElementCountQueue.put(elementString,queue);
                    }
                }
            }
        }

        //用hashmap记录拓扑结构的子树中，每个POS出现在子树字符串中的下标；
        //查看每个POS出现的次数，若大于一次，则在POS后面加上“1”、“2”、“3”...的记号；
        HashMap<String, Integer> patternElementIndex = new HashMap<>();
        for(int i=0;i<countList.size();i++)
        {
            if(countList.get(i)==savtree[0]||countList.get(i)==savtree[1])
            {
                int index = noList.get(i);
                String elementString = "";
                for(int j=index;j<index+5;j++)
                    elementString+=subtreeStringArray[j];
                elementString = elementString.split("\\[")[1].trim();
                //表示为子树节点；
                if(countList.get(i)==savtree[1])
                {
                    if(!patternElementCountQueue.containsKey(elementString))
                    {
                        patternElements.add(elementString);
                        patternElementIndex.put(elementString,index+1);
                    }
                    else
                    {
                        Queue<Integer> queue = patternElementCountQueue.get(elementString);
                        int posCount = queue.poll();
                        patternElementCountQueue.put(elementString, queue);
                        if(!(queue.isEmpty()&&posCount==1))
                            elementString += posCount;
                        patternElementIndex.put(elementString,index+1);
                        patternElements.add(elementString);
                    }
                }
                else if(countList.get(i)==savtree[0])
                    patternElements.add(elementString);
            }
        }

        for(int i=0;i<patternElements.size();i++)
        {
            if(i==0)
                topologicalPatternString += patternElements.get(i).replace(" ","") + "->";
            else if(i==patternElements.size()-1)
                topologicalPatternString += patternElements.get(i).replace(" ","");
            else
                topologicalPatternString += patternElements.get(i).replace(" ","") + "+";
        }

        ArrayList<ArrayList<String>> predicateMentionList = findPredicateMention(topologicalPatternString);

        if(predicateMentionList.size()!=0)
        {
            for(ArrayList<String> predicateMention : predicateMentionList)
            {
                ArrayList<String> predicateMentionWords = new ArrayList<>();
                if(predicateMention.size()==0)
                    predicateMentionWordList.add(predicateMentionWords);
                else
                {
                    //返回谓词指称对应的句子成分;
                    for(String predicatemention : predicateMention)
                    {
                        int index = patternElementIndex.get(predicatemention);
                        //谓词指称在句法树中的层数，只将其后续大于该层的那些words输出，表示为这个谓词指称的子树成分;
                        int layer = savtree[1];
                        int layerCount = layer;
                        String output = "";
                        for(int i = index; i<subtreeStringArray.length;i++)
                        {
                            if(subtreeStringArray[i]=='[')
                                layerCount++;
                            if(subtreeStringArray[i]==']')
                            {
                                layerCount--;
                                output += ' ';
                                if(layerCount<layer)
                                    break;
                            }
                            if(isChinese(subtreeStringArray[i]))
                                output += subtreeStringArray[i];
                        }
                        output = output.trim();
                        String[] outputs = output.split(" ");
                        for (String s : outputs)
                        {
                            predicateMentionWords.add(s);
                        }
                    }
                    ArrayList<String> refinedWords = new ArrayList<>();
                    //停用词与""都需要去掉
                    HashSet<String> stopwords = MoveStopwords.getInstance().getStopwordSet();
                    for (int i = 0; i < predicateMentionWords.size(); i++) {
                        String s = predicateMentionWords.get(i).toString();
                        if (!stopwords.isEmpty()) {
                            if (!stopwords.contains(s)&&!s.equalsIgnoreCase("")) {
                                refinedWords.add(s);
                            }
                        }
                    }
                    predicateMentionWordList.add(refinedWords);
                }
            }

        }

        return predicateMentionWordList;
    }

    public boolean isChinese(char c) {
        return c >= 0x4E00 &&  c <= 0x9FA5;// 根据字节码判断
    }

    public ArrayList<ArrayList<String>> findPredicateMention(String topologicalPatternString)
    {
        ArrayList<ArrayList<String>> predicateMentionList = new ArrayList<>();
        if(topologicalPatternString.split("->").length==1)
            return predicateMentionList;
        String root = topologicalPatternString.split("->")[0];
        String[] leaves = topologicalPatternString.split("->")[1].trim().split("\\+");
        ArrayList<String> leavePOS = new ArrayList<>();
        for(String temp : leaves)
            leavePOS.add(temp);
        TopologicalPattern topologicalPattern = new TopologicalPattern();
        topologicalPattern.setRoot_POS(root);
        topologicalPattern.setLeaves_POS(leavePOS);

        ArrayList<TopologicalStructure> topologicalStructureRepository
                = TopologicalPatternClient.getInstance().getTopologicalStructureRepository();

        for(TopologicalStructure topologicalStructure : topologicalStructureRepository)
        {
            if(topologicalPattern.sameTopologicalPattern(topologicalStructure.getTopologicalPattern()))
            {
                ArrayList<String> predicateMention;
                predicateMention = topologicalStructure.getPredicate_mention();
                predicateMentionList.add(predicateMention);
            }
        }
        return predicateMentionList;
    }

    public ArrayList<ArrayList<String>> getPredicateMention(String posSequence, String[] sentence)
    {
        ArrayList<ArrayList<String>> predicateMentionList;

        String subtreeString = this.extractSubTree(posSequence, sentence);

        predicateMentionList = buildTopologicalPattern(subtreeString);

        return predicateMentionList;
    }


}
