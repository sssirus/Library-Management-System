package com.qa.demo.templateTraining;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TemplateGeneralizationTest {



    @Test
    void printPredicateTemplateSegmentationMappings() {

        try {
            TemplateGeneralization.getInstance().printPredicateTemplateSegmentationMappings();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void printPredicateSynonymsMappings() {

        try {
            TemplateGeneralization.getInstance().printPredicateSynonymsMappings();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void printPredicateTemplateMappings() {

        try {
            TemplateGeneralization.getInstance().printPredicateSynonymsMappings();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            TemplateGeneralization.getInstance().printPredicateTemplateMappings();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            TemplateGeneralization.getInstance().printPredicateTemplateSegmentationMappings();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}