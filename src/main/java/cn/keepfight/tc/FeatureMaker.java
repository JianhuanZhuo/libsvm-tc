package cn.keepfight.tc;

import com.chenlb.mmseg4j.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Created by 卓建欢 on 2017/12/21.
 */
public class FeatureMaker {

    public void xx(List<String> strList) throws IOException {
        Dictionary dic = Dictionary.getInstance();
        DicMap dicMap = DicMap.getInstance();
        long start = System.currentTimeMillis();
        strList.stream()
                .map(StringReader::new)
                .map(sr -> new MMSeg(sr, new MaxWordSeg(dic)))
                .forEach(mmSeg -> {
                    Word word = null;
                    try {
                        while ((word = mmSeg.next()) != null) {
                            dicMap.make(word.getString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        long end = System.currentTimeMillis();
        System.out.println("cost time : "+(end-start));
    }

    public static void main(String[] args) throws IOException {
        LabeledProcessor p = new LabeledProcessor();
        FeatureMaker fm = new FeatureMaker();
        List<String> xx = p.getLabeledText();
        System.out.println("xx :" + xx.size());
        fm.xx(xx);
        DicMap.getInstance().write();
    }
}
