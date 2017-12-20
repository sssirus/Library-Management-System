package com.qa.demo.utils.lucene;

import org.ansj.library.*;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.recognition.impl.SynonymsRecgnition;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.SmartForest;
import org.nlpcn.commons.lang.util.StringUtil;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TT. Wu on 2017/5/15.
 */
public class AgricultureAnalyzer extends Analyzer {
    public static final Log LOG = LogFactory.getLog();
    private Map<String, String> args;

    public AgricultureAnalyzer(Map<String, String> args) {
        this.args = args;
    }

    public AgricultureAnalyzer(TYPE type, String dics) {
        this.args = new HashMap();
        this.args.put("type", type.name());
        this.args.put("dic", dics);
    }

    public AgricultureAnalyzer(TYPE type) {
        this.args = new HashMap();
        this.args.put("type", type.name());
    }

    protected TokenStreamComponents createComponents(String text) {
        BufferedReader reader = new BufferedReader(new StringReader(text));
        Tokenizer tokenizer = null;
        tokenizer = getTokenizer(reader, this.args);
        return new TokenStreamComponents(tokenizer);
    }

    public static Tokenizer getTokenizer(Reader reader, Map<String, String> args) {
        if(LOG.isDebugEnabled()) {
            LOG.debug("to create tokenizer " + args);
        }

        Object analysis = null;

        Analysis analysis1 = null;

        String temp = null;
        String type = (String)args.get("type");
        if(type == null) {
            type = TYPE.BASE.name();
        }

        switch(TYPE.valueOf(type).ordinal()) {
            case 1:
                analysis1 = new BaseAnalysis();
                analysis = analysis1;
                break;
            case 2:
                //添加词典
                analysis1 = new IndexAnalysis();
                analysis1.setForests();
                analysis = analysis1;
                break;
            case 3:
                //添加词典
                analysis1 = new DicAnalysis();
                analysis1.setForests();
                analysis = analysis1;
                break;
            case 4:
                //添加词典
                analysis1 = new ToAnalysis();
                analysis1.setForests();
                analysis = analysis1;
                break;
            case 5:
                analysis = new NlpAnalysis();
                if(StringUtil.isNotBlank(temp = (String)args.get("crf"))) {
                    ((NlpAnalysis)analysis).setCrfModel(CrfLibrary.get(temp));
                }
                break;
            default:
                analysis = new BaseAnalysis();
        }

        if(reader != null) {
            ((Analysis)analysis).resetContent(reader);
        }

        if(StringUtil.isNotBlank(temp = (String)args.get("dic"))) {
            String[] filters = temp.split(",");
            Forest[] synonyms = new Forest[filters.length];

            for(int split = 0; split < synonyms.length; ++split) {
                if(!StringUtil.isBlank(filters[split])) {
                    synonyms[split] = DicLibrary.get(filters[split]);
                }
            }

            ((Analysis)analysis).setForests(synonyms);
        }

        ArrayList var13 = null;
        int var9;
        String[] var16;
        if(StringUtil.isNotBlank(temp = (String)args.get("stop"))) {
            String[] var14 = temp.split(",");
            var13 = new ArrayList();
            var16 = var14;
            int var8 = var14.length;

            for(var9 = 0; var9 < var8; ++var9) {
                String key = var16[var9];
                StopRecognition key1 = StopLibrary.get(key.trim());
                if(key1 != null) {
                    var13.add(key1);
                }
            }
        }

        ArrayList var15 = null;
        if(StringUtil.isNotBlank(temp = (String)args.get("synonyms"))) {
            var16 = temp.split(",");
            var15 = new ArrayList();
            String[] var17 = var16;
            var9 = var16.length;

            for(int var18 = 0; var18 < var9; ++var18) {
                String var19 = var17[var18];
                SmartForest sf = SynonymsLibrary.get(var19.trim());
                if(sf != null) {
                    var15.add(new SynonymsRecgnition(sf));
                }
            }
        }

        if(StringUtil.isNotBlank(temp = (String)args.get("ambiguity"))) {
            ((Analysis)analysis).setAmbiguityForest(AmbiguityLibrary.get(temp.trim()));
        }

        if(StringUtil.isNotBlank(temp = (String)args.get("isNameRecognition"))) {
            ((Analysis)analysis).setIsNameRecognition(Boolean.valueOf(temp));
        }

        if(StringUtil.isNotBlank(temp = (String)args.get("isNumRecognition"))) {
            ((Analysis)analysis).setIsNumRecognition(Boolean.valueOf(temp));
        }

        if(StringUtil.isNotBlank(temp = (String)args.get("isQuantifierRecognition"))) {
            ((Analysis)analysis).setIsQuantifierRecognition(Boolean.valueOf(temp));
        }

        if(StringUtil.isNotBlank(temp = (String)args.get("isRealName"))) {
            ((Analysis)analysis).setIsRealName(Boolean.valueOf(temp));
        }

        return new AgricultureTokenizer((Analysis)analysis, var13, var15);
    }

    public static enum TYPE {
        BASE,
        INDEX,
        QUERY,
        DIC,
        NLP;

        private TYPE() {
        }
    }
}
