package cn.keepfight.tc;

import org.junit.Test;

import java.util.List;

/**
 * Created by 卓建欢 on 2017/12/27.
 */
public class TfIdfTest {
    @Test
    public void tfidf() throws Exception {

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
        List<Pair<Integer, String>> list = new LabeledProcessor().getLabeled();

        System.out.println("time 4 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        TfIdf tfIdf = new TfIdf(xx);
        tfIdf.tfidf(list);

        System.out.println("time 5 : " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
    }
}