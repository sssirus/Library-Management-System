package com.qa.demo.utils.w2v;

import java.io.*;
import java.util.*;

/**
 * @author J.Y.Zhang
 * @create 2018-03-30
 * Function description:
 **/
public class Word2VEC {

    private HashMap<String, double[]> wordMap = new HashMap<String, double[]>();

    private int words;
    private int size;
    private int topNSize = 40;

    /**
     * 加载模型
     *
     * @param path
     *            模型的路径
     * @throws IOException
     */
    public void loadGoogleModel(String path) throws IOException {
        DataInputStream dis = null;
        BufferedInputStream bis = null;
        double len = 0;
        double vector = 0;
        try {
            bis = new BufferedInputStream(new FileInputStream(path));
            dis = new DataInputStream(bis);
            // //读取词数
            words = Integer.parseInt(readString(dis));
            // //大小
            size = Integer.parseInt(readString(dis));
            String word;
            double[] vectors = null;
            for (int i = 0; i < words; i++) {
                word = readString(dis);
                vectors = new double[size];
                len = 0;
                for (int j = 0; j < size; j++) {
                    vector = readFloat(dis);
                    len += vector * vector;
                    vectors[j] = (double) vector;
                }
                len = Math.sqrt(len);

                for (int j = 0; j < size; j++) {
                    vectors[j] /= len;
                }

                wordMap.put(word, vectors);
                dis.read();
            }
        } finally {
            bis.close();
            dis.close();
        }
    }

    /**
     * 加载模型
     *
     * @param path
     *            模型的路径
     * @throws IOException
     */
    public void loadJavaModel(String path) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
            words = dis.readInt();
            size = dis.readInt();

            double vector = 0;

            String key = null;
            double[] value = null;
            for (int i = 0; i < words; i++) {
                double len = 0;
                key = dis.readUTF();
                value = new double[size];
                for (int j = 0; j < size; j++) {
                    vector = dis.readFloat();
                    len += vector * vector;
                    value[j] = vector;
                }

                len = Math.sqrt(len);

                for (int j = 0; j < size; j++) {
                    value[j] /= len;
                }
                wordMap.put(key, value);
            }

        }
    }

    private static final int MAX_SIZE = 50;

    /**
     * 近义词
     *
     * @return
     */
    public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
        double[] wv0 = getWordVector(word0);
        double[] wv1 = getWordVector(word1);
        double[] wv2 = getWordVector(word2);

        if (wv1 == null || wv2 == null || wv0 == null) {
            return null;
        }
        double[] wordVector = new double[size];
        for (int i = 0; i < size; i++) {
            wordVector[i] = wv1[i] - wv0[i] + wv2[i];
        }
        double[] tempVector;
        String name;
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
        for (Map.Entry<String, double[]> entry : wordMap.entrySet()) {
            name = entry.getKey();
            if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
                continue;
            }
            double dist = 0;
            tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++) {
                dist += wordVector[i] * tempVector[i];
            }
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<WordEntry>(wordEntrys);
    }

    private void insertTopN(String name, double score, List<WordEntry> wordsEntrys) {
        // TODO Auto-generated method stub
        if (wordsEntrys.size() < topNSize) {
            wordsEntrys.add(new WordEntry(name, score));
            return;
        }
        double min = Float.MAX_VALUE;
        int minOffe = 0;
        for (int i = 0; i < topNSize; i++) {
            WordEntry wordEntry = wordsEntrys.get(i);
            if (min > wordEntry.score) {
                min = wordEntry.score;
                minOffe = i;
            }
        }

        if (score > min) {
            wordsEntrys.set(minOffe, new WordEntry(name, score));
        }

    }

    public Set<WordEntry> distance(String queryWord) {

        double[] center = wordMap.get(queryWord);
        if (center == null) {
            return Collections.emptySet();
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
        TreeSet<WordEntry> result = new TreeSet<WordEntry>();

        double min = Float.MIN_VALUE;
        for (Map.Entry<String, double[]> entry : wordMap.entrySet()) {
            double[] vector = entry.getValue();
            double dist = 0;
            for (int i = 0; i < vector.length; i++) {
                dist += center[i] * vector[i];
            }

            if (dist > min) {
                result.add(new WordEntry(entry.getKey(), dist));
                if (resultSize < result.size()) {
                    result.pollLast();
                }
                min = result.last().score;
            }
        }
        result.pollFirst();

        return result;
    }

    public Set<WordEntry> distance(List<String> words) {

        double[] center = null;
        for (String word : words) {
            center = sum(center, wordMap.get(word));
        }

        if (center == null) {
            return Collections.emptySet();
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
        TreeSet<WordEntry> result = new TreeSet<WordEntry>();

        double min = Float.MIN_VALUE;
        for (Map.Entry<String, double[]> entry : wordMap.entrySet()) {
            double[] vector = entry.getValue();
            double dist = 0;
            for (int i = 0; i < vector.length; i++) {
                dist += center[i] * vector[i];
            }

            if (dist > min) {
                result.add(new WordEntry(entry.getKey(), dist));
                if (resultSize < result.size()) {
                    result.pollLast();
                }
                min = result.last().score;
            }
        }
        result.pollFirst();

        return result;
    }

    private double[] sum(double[] center, double[] fs) {
        // TODO Auto-generated method stub

        if (center == null && fs == null) {
            return null;
        }

        if (fs == null) {
            return center;
        }

        if (center == null) {
            return fs;
        }

        for (int i = 0; i < fs.length; i++) {
            center[i] += fs[i];
        }

        return center;
    }

    /**
     * 得到词向量
     *
     * @param word
     * @return
     */
    public double[] getWordVector(String word) {
        return wordMap.get(word);
    }

    public static float readFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }

    /**
     * 读取一个float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    /**
     * 读取一个字符串
     *
     * @param dis
     * @return
     * @throws IOException
     */
    private static String readString(DataInputStream dis) throws IOException {
        // TODO Auto-generated method stub
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }

    public int getTopNSize() {
        return topNSize;
    }

    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public HashMap<String, double[]> getWordMap() {
        return wordMap;
    }

    public int getWords() {
        return words;
    }

    public int getSize() {
        return size;
    }

}

