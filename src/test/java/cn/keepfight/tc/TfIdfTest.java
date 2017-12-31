package cn.keepfight.tc;

import com.chenlb.mmseg4j.Dictionary;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 卓建欢 on 2017/12/27.
 */
public class TfIdfTest {
    @Test
    public void generateTrain() throws Exception {
        Dictionary.getInstance();
        long t = System.currentTimeMillis();
        LabeledProcessor p = new LabeledProcessor();
//        FeatureMaker fm = new FeatureMaker();
        List<String> xx = p.getLabeledText();
        System.out.println("xx :" + xx.size());
        System.out.println("time 1 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
//        fm.xx(xx);
        DicMap.getInstance().read();
        System.out.println("time 2 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        DicMap.getInstance().write();
        System.out.println("time 3 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        List<Pair<Integer, String>> list = p.getLabeled();

        System.out.println("time 4 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        TfIdf tfIdf = new TfIdf(xx);

        System.out.println("time 4.5 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();

        List<Pair<Integer, Map<Integer, Double>>> res = tfIdf.tfidf(list);

        System.out.println("time 5 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();

        FileUtils.writeLines(new File("data/tfidf_2.train"),
                res.parallelStream()
                        .map(pair -> {
                            String str = pair.getV().entrySet().stream()
                                    .map(x -> x.getKey() + ":" + x.getValue())
                                    .collect(Collectors.joining(" "));
                            if (pair.getK() == 0) {
                                str = "-1 " + str;
                            } else {
                                str = "+1 " + str;
                            }
                            return str;
                        })
                        .collect(Collectors.toList())
        );

        System.out.println("time end : " + (System.currentTimeMillis() - t));
    }


    @Test
    public void generateTest() throws Exception {
        Dictionary.getInstance();
        long t = System.currentTimeMillis();
        LabeledProcessor p = new LabeledProcessor();
        List<String> xx = p.getTestedText();
        System.out.println("xx :" + xx.size());
        System.out.println("time 1 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        DicMap.getInstance().read();
        System.out.println("time 2 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        DicMap.getInstance().write();
        System.out.println("time 3 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        List<Pair<Integer, String>> list = p.getTested();

        System.out.println("time 4 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        TfIdf tfIdf = new TfIdf(xx);

        System.out.println("time 4.5 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();

        List<Pair<Integer, Map<Integer, Double>>> res = tfIdf.tfidf(list);

        System.out.println("time 5 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();

        FileUtils.writeLines(new File("data/tfidf_2.test"),
                res.parallelStream()
                        .map(pair -> {
                            String str = pair.getV().entrySet().stream()
                                    .map(x -> x.getKey() + ":" + x.getValue())
                                    .collect(Collectors.joining(" "));
                            if (pair.getK() == 0) {
                                str = "-1 " + str;
                            } else {
                                str = "+1 " + str;
                            }
                            return str;
                        })
                        .collect(Collectors.toList())
        );

        System.out.println("time end : " + (System.currentTimeMillis() - t));
    }

    @Test
    public void train() throws IOException {

        String[] arg = {"data\\tfidf.train", // 存放SVM训练模型用的数据的路径
                "data\\tfidf.train.model"};  //练出来的模型的路径
        System.out.println("........SVM运行开始..........");
        svm_train.main(arg); // 调用
    }

    @Test
    public void test() throws IOException {
        String[] parg = {"data\\tfidf.test", // 这个是存放测试数据
                "data\\tfidf.train.model", // 调用的是训练以后的模型
                "data\\tfidf.out"}; // 生成的结果的文件的路径
        System.out.println("........SVM运行开始..........");
        svm_predict.main(parg); // 调用
    }
}