package cn.keepfight.tc;

import com.chenlb.mmseg4j.*;

import java.io.*;

/**
 * Created by 卓建欢 on 2017/12/17.
 */
public class Main {

    public static String segWords(Reader input, String wordSpilt) throws IOException {
        StringBuilder sb = new StringBuilder();
        Seg seg = new MaxWordSeg(Dictionary.getInstance());	//取得不同的分词具体算法
        MMSeg mmSeg = new MMSeg(input, seg);
        Word word = null;
        boolean first = true;
        while((word=mmSeg.next())!=null) {
            if(!first) {
                sb.append(wordSpilt);
            }
            String w = word.getString();
            sb.append(w);
            first = false;

        }
        return sb.toString();
    }

    public static String segWords(String txt, String wordSpilt) throws IOException {
        return segWords(new StringReader(txt), wordSpilt);
    }

    public static void main(String[] args) throws IOException {
//        // TODO Auto-generated method stub
//        String[] arg = {"data\\a1a.train", // 存放SVM训练模型用的数据的路径
//                "data\\a1a.train.model"};  //练出来的模型的路径
//
//        String[] parg = {"data\\a1a.test", // 这个是存放测试数据
//                "data\\a1a.train.model", // 调用的是训练以后的模型
//                "data\\a1a.out"}; // 生成的结果的文件的路径
//        System.out.println("........SVM运行开始..........");
//        svm_train.main(arg); // 调用
//        svm_predict.main(parg); // 调用

        String txt = "京华时报2018年1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
        if (args.length > 0) {
            txt = args[0];
        }
        System.out.println(segWords(txt, " | "));
    }
}
