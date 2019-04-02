package com.qa.demo.utils.kbgeneration;
/**
 * Created by hyh on 2017/8/25.
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class KBGeneration {

    private HashMap<String, String> onelevel = new HashMap<String, String>();
    public String baseurl = "http://tupu.zgny.com.cn";
    private HashMap<String, String> Lastlevel = new HashMap<String, String>();
    public BufferedWriter out = null;


    //抽取第一层信息
    public void getOnelevel() throws IOException {
        Document doc = Jsoup.connect("http://tupu.zgny.com.cn/list_baike.aspx").timeout(5000).get();
        Element content = doc.getElementsByClass("bigzhans_list_left_txt").first();
        Elements links = content.getElementsByTag("a");
        for (Element link : links) {
            String linkHref = link.attr("href");
            String linkText = link.text();
            linkText = linkText.substring(2, linkText.length());
            linkHref = "http://tupu.zgny.com.cn" + linkHref;
            onelevel.put(linkText, linkHref);
            getTwolevel(linkHref, linkText);
            writeClassAndSubclass("农业", linkText);
        }
        System.out.println(Lastlevel.size());
    }

    //抽取第二层信息
    public void getTwolevel(String onelevelurl, String onelevelclass) throws IOException {
        Document doc = Jsoup.connect(onelevelurl).timeout(5000).get();
        Elements elements = doc.getElementsByClass("bigzhans_list_right_txt");
        Elements contents = elements.first().getElementsByClass("mc3");
        for (int i = 0; i < contents.size(); i++) {
            Element element = contents.get(i);
            Element link = element.getElementsByTag("a").first();
            String linkHref = link.attr("href");
            String linkText = link.text();
//            System.out.println(baseurl+linkHref);
//            System.out.println(linkText);


            fenye(baseurl + linkHref, linkText, onelevelclass);

            if (onelevelclass.contains("农作物病虫害")) {
                if (linkText.contains("害") || linkText.contains("天敌")) {

                } else {
                    linkText = linkText + "病虫害";
                }
            } else if (onelevelclass.contains("猪病图谱")) {
                linkText = linkText.substring(0, linkText.length() - 2);
            } else if (onelevelclass.contains("疾病防治图谱")) {
                if (linkText.contains("其他")) {
                    linkText = linkText + "病防治";
                }
            } else if (onelevelclass.contains("兽药图谱")) {
                if (linkText.contains("其他")) {
                    linkText = linkText + "药";
                }
            }

            writeClassAndSubclass(onelevelclass, linkText);


        }

    }

    //得到所有分页信息
    public void fenye(String twolevelurl, String twolevelclass, String onelevelclass) throws IOException {
        Document doc = Jsoup.connect(twolevelurl).timeout(5000).get();
        Element element = doc.getElementById("SumNum");
        Elements links = element.getElementsByTag("a");
        for (Element link : links) {
            String linkHref = link.attr("href");
            String linkText = link.text();
//            System.out.println(baseurl+linkHref);
//            System.out.println(linkText);
            getThreelevel(baseurl + linkHref, twolevelclass, onelevelclass);

        }
    }

    //抽取第三层信息
    public void getThreelevel(String twolevelurl, String twolevelclass, String onelevelclass) throws IOException {
        Document doc = Jsoup.connect(twolevelurl).timeout(5000).get();
        Element element = doc.getElementsByClass("home-list3ccc").first();
        Elements links = element.getElementsByTag("a");
        for (Element link : links) {
            String linkHref = link.attr("href");
            String linkText = link.text();
//            System.out.println(linkHref);
//            System.out.println(linkText);
//            System.out.println(twolevelclass);
//            System.out.println(onelevelclass);
            Lastlevel.put(linkText, linkText);
            String entity = getDetail(linkHref, twolevelclass, onelevelclass);
            if (!entity.equals("")) {
                writeClassAndEntity(twolevelclass, entity);
            }
        }
    }

    //得到页面详细信息
    public String getDetail(String url, String twolevelclass, String onelevelclass) throws IOException {
        String ee = "";
        try {
            Document doc = Jsoup.connect(url).timeout(5000).get();
            Element element = doc.getElementsByClass("wenZi_02").first();
            String entity = doc.getElementsByTag("h1").first().text();
            if (entity.contains("（图）")) {
                ee = entity.substring(0, entity.indexOf("（"));
            }
            if (entity.contains("(图)")) {
                ee = entity.substring(0, entity.indexOf("("));
            }
            Elements elements = element.getElementsByTag("p");
            if (elements.size() > 0) {
                System.out.println(entity);
                System.out.println(onelevelclass);
                if (onelevelclass.contains("花木图谱")) {
                    parseHtmlDetail(entity, elements);
                }
                if (onelevelclass.contains("农业百科")) {
                    parseHtmlInAll(entity, element);
                }
                if (onelevelclass.contains("农作物病虫害")) {
                    parseHtmlInAll(entity, element);
                }
                if (onelevelclass.contains("作物品种")) {
                    parseHtmlInAll(entity, element);
                }
                if (onelevelclass.contains("水产图谱")) {
                    parseHtmlInAll(entity, element);
                }
                if (onelevelclass.contains("猪病图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("蔬菜图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("农作物病虫害")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("食用菌图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("畜禽图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("农药图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("农机图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("疾病防治图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("特养图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("兽药图谱")) {
                    parseHtmlMedicine(entity, elements);
                }

                if (onelevelclass.contains("饲料配方")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("果品图谱")) {
                    parseHtmlDetail(entity, elements);
                }

                if (onelevelclass.contains("中草药图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("种业图谱")) {
                    parseHtmlInAll(entity, element);
                }

                if (onelevelclass.contains("肥料图谱")) {
                    parseHtmlInAll(entity, element);
                }


            } else {
                parseHtmlInAll(entity, element);
            }

            return entity;
        } catch (Exception e) {

        }

        return ee;

    }

    //详细解析页面
    public void parseHtmlDetail(String entity, Elements elements) throws IOException {
        String attribute = "";
        String value = "";
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String s = element.text();
            s = s.replace(" ", "");
            if (s.contains(":")) {
                attribute = s.split(":")[0];
                if (!s.endsWith(":")) {
                    value = s.split(":")[1];
                }
                if (elements.get(i + 1).text().contains(":")) {
                    writeEntityAndAttribute(entity, attribute, value);
                }


            } else {
                value = value + s;
                if (elements.get(i + 1).text().contains(":")) {
                    writeEntityAndAttribute(entity, attribute, value);
                }

            }


        }
    }

    //解析药物页面
    public void parseHtmlMedicine(String entity, Elements elements) throws IOException {
        String attribute = "";
        String value = "";
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String s = element.text();
            s = s.replace(" ", "");
            if (s.contains("】")) {
                attribute = s.substring(s.indexOf("【") + 1, s.indexOf("】"));
                if (!s.endsWith("】")) {
                    value = s.split("】")[1];
                }
                if (elements.get(i + 1).text().contains("】")) {
                    writeEntityAndAttribute(entity, attribute, value);
                }


            } else {
                value = value + s;
                if (elements.get(i + 1).text().contains("】")) {
                    writeEntityAndAttribute(entity, attribute, value);
                }

            }


        }
    }

    //粗略解析页面
    public void parseHtmlInAll(String entity, Element element) throws IOException {
        writeEntityAndAttribute(entity, "描述", element.text());

    }

    //写入类和子类
    public void writeClassAndSubclass(String onelevelclass, String twolevelclass) throws IOException {
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\class-subclass.txt", true)));
        out.write("<category:" + twolevelclass + "> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <category:" + onelevelclass + "> ." + "\r\n");
        out.close();
    }

    //写入类和实体
    public void writeClassAndEntity(String cla, String entity) throws IOException {
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\class-entity.txt", true)));
        out.write("<http://coindb/tupu.zgny/resource/" + entity + "> <http://coindb/ontology/category> <http://coindb/tupu.zgny/category/" + cla + ">" + "\r\n");
        out.close();
    }


    //写入实体和属性
    public void writeEntityAndAttribute(String entity, String attribute, String value) throws IOException {
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\entity-attribute.txt", true)));
        out.write("<http://coindb/tupu.zgny/resource/" + entity + "> <http://coindb/tupu.zgny/property/" + attribute + "> " + value + "\r\n");
        out.close();

    }

}
