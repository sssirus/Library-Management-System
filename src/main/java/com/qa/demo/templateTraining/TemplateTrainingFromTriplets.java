package com.qa.demo.templateTraining;

import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.Predicate;
import com.qa.demo.dataStructure.QuestionTemplate;
import com.qa.demo.dataStructure.Triplet;
import com.qa.demo.utils.kgprocess.KGTripletsClient;
import com.qa.demo.utils.qgeneration.KBTripletBasedQuestionGeneration;

import java.util.*;

public class TemplateTrainingFromTriplets {

    private static HashSet<QuestionTemplate> generateQuestionTemplates(ArrayList<Triplet> triplets){
        HashSet<QuestionTemplate> questionTemplates = new HashSet<>();
        HashMap<String, ArrayList<Predicate>> shelter = new HashMap<>();
        if(triplets.size()==0||triplets.isEmpty()||triplets==null)
            return questionTemplates;
        else{
            for(Triplet t:triplets)
            {
                HashSet<String> questionTemplateString = KBTripletBasedQuestionGeneration.
                        questionTemplates(t.getSubjectName(), t.getPredicateName());
                for(String temp : questionTemplateString) {
                    temp = temp.replace(t.getSubjectName(), Configuration.SPLITSTRING);
                    for(String punctuation : Configuration.PUNCTUATION_SET)
                    {
                       temp = temp.replace(punctuation,"");
                    }
                    temp = temp.trim();
                    if (shelter.containsKey(temp)) {
                        ArrayList<Predicate> predicatelist = shelter.get(temp);
                        boolean flag = false;
                        for (Predicate p : predicatelist) {
                            if (p.getKgPredicateName().equalsIgnoreCase(t.getPredicateName())) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag)
                            continue;
                        else {
                            Predicate predicate = new Predicate();
                            predicate.setPredicateURI(t.getPredicateURI());
                            predicate.setMentionName(t.getPredicateName());
                            predicate.setKgPredicateName(t.getPredicateName());
                            predicatelist.add(predicate);
                            shelter.put(temp, predicatelist);
                        }
                    } else {
                        Predicate predicate = new Predicate();
                        predicate.setPredicateURI(t.getPredicateURI());
                        predicate.setMentionName(t.getPredicateName());
                        predicate.setKgPredicateName(t.getPredicateName());
                        ArrayList<Predicate> plist = new ArrayList<Predicate>();
                        plist.add(predicate);
                        shelter.put(temp, plist);
                    }
                }
            }
        }

        Iterator it = shelter.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, ArrayList<Predicate>> entry =
                    (Map.Entry<String, ArrayList<Predicate>>)it.next();
            for(Predicate p : entry.getValue())
            {
                QuestionTemplate template = new QuestionTemplate();
                template.setTemplateString(entry.getKey());
                template.setPredicate(p);
                questionTemplates.add(template);
            }
        }
        return questionTemplates;
    }

    public static HashSet<QuestionTemplate> mainDriver(){

        ArrayList<Triplet> triplets = KGTripletsClient.getInstance().getKgTriplets();
        return(generateQuestionTemplates(triplets));
    }

}
