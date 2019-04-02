package com.qa.demo.utils;
/**
 *  Created time: 2017_08_30
 *  Author: Devin Hua
 *  Function description:
 *  The main driver interface for KG-relevant utils.
 */

import com.qa.demo.dataStructure.Triplet;

import java.util.List;

public interface KGUtilDriver {

    //从文件资源中读取所有的实体列表，并写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    List<?> getEntityListFromFile();

    //从文件资源中读取所有的属性列表，并写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    List<?> getPropertyListFromFile();

    //对所有的问题做entity linking，并写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    List<?> getEntityLinkingFromFile();

    //对所有的问题做property linking，并写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    //此处为一个问题链接到了多少属性；
    List<?> getQuestionPropertyLinkingFromFile();

    //对所有的问题做property linking，并写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    //此处为一个属性链接到了多少问题；
    List<?> getPropertyQuestionLinkingFromFile();

    //对所有的问题做property linking，并找到所有不包含任何KG中属性的问题，且写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    List<?> getQuestionWithoutPropertyFromFile();

    //对所有的问题做property linking，并找到所有不包含于任何问题的KG属性，且写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    List<?> getPropertyWithoutQuestionFromFile();

    //统计所有谓语为对象属性的三元组，并写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    List<Triplet> getObjectPropertyTripletsFromFile();

    //统计所有谓语为对象属性的三元组，并写入文件；
    //要求将输入、输出文件地址固化到conf/FileConfig中，并使用相对路径。
    List<Triplet> getDatatypePropertyTripletsFromFile();

}
