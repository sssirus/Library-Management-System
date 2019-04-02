package com.qa.demo.utils.lucene;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.recognition.impl.SynonymsRecgnition;
import org.ansj.splitWord.Analysis;
import org.ansj.util.AnsjReader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TT. Wu on 2017/5/15.
 */
public class AgricultureTokenizer extends Tokenizer {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    //private final PositionIncrementAttribute positionAttr = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    protected Analysis ta = null;
    private LinkedList<Object> result;
    private List<StopRecognition> stops;
    private List<SynonymsRecgnition> synonyms;
    long position = 0;

    public AgricultureTokenizer(Analysis ta, List<StopRecognition> stops, List<SynonymsRecgnition> synonyms) {
        this.ta = ta;
        this.stops = stops;
        this.synonyms = synonyms;
        //设置词典
        ta.setForests();
    }

    public final boolean incrementToken() throws IOException {
        if(this.result == null) {
            this.parse();
        }

        Object obj = this.result.pollFirst();
        if(obj == null) {
            this.result = null;
            return false;
        } else {
            if(obj instanceof Term) {
                this.clearAttributes();

                Term term;
                for(term = (Term)obj; this.filterTerm(term); ++this.position) {
                    term = (Term)this.result.pollFirst();
                    if(term == null) {
                        this.result = null;
                        return false;
                    }
                }

                ++this.position;
                List synonyms = term.getSynonyms();
                String rName = null;
                if(synonyms == null) {
                    rName = term.getName();
                } else {
                    for(int i = 1; i < synonyms.size(); ++i) {
                        this.result.addFirst(synonyms.get(i));
                    }

                    rName = (String)synonyms.get(0);
                }

                this.offsetAtt.setOffset(term.getOffe(), term.getOffe() + term.getName().length());
                this.typeAtt.setType(term.getNatureStr());
                //this.positionAttr.setPositionIncrement(this.position);
                this.termAtt.setEmpty().append(rName);
            } else {
//                this.positionAttr.setPositionIncrement(this.position);
                this.termAtt.setEmpty().append(obj.toString());
            }

            return true;
        }
    }

    private boolean filterTerm(Term term) {
        if(this.stops != null && this.stops.size() > 0) {
            Iterator var2 = this.stops.iterator();

            while(var2.hasNext()) {
                StopRecognition filterRecognition = (StopRecognition)var2.next();
                if(filterRecognition.filter(term)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.ta.resetContent(new AnsjReader(this.input));
        this.parse();
    }

    private void parse() throws IOException {
        Result parse = this.ta.parse();
        if(this.synonyms != null) {
            Iterator var2 = this.synonyms.iterator();

            while(var2.hasNext()) {
                SynonymsRecgnition sr = (SynonymsRecgnition)var2.next();
                parse.recognition(sr);
            }
        }

        this.result = new LinkedList(parse.getTerms());
    }
}
