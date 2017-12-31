package cn.keepfight.tc;

import com.chenlb.mmseg4j.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by 卓建欢 on 2017/12/21.
 */
public class FeatureMaker {

    private Pattern pattern = Pattern.compile("\\w+");

    public void xx(List<String> strList) throws IOException {
        Dictionary dic = Dictionary.getInstance();
        DicMap dicMap = DicMap.getInstance();
        long start = System.currentTimeMillis();
        Map<String, Long> numMap = strList.parallelStream()
                .map(StringReader::new)
                .map(sr -> new MMSeg(sr, new MaxWordSeg(dic)))
                .flatMap(mmSeg -> {
                    List<String> wds = new ArrayList<>();
                    Word word = null;
                    try {
                        while ((word = mmSeg.next()) != null) {
//                            dicMap.make(word.getString());
                            wds.add(word.getString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return wds.stream();
                })
                .filter(s->!pattern.matcher(s).matches())
                .collect(Collectors.groupingBy(str -> str, Collectors.counting()));

        numMap = numMap.entrySet()
                .parallelStream()
                .filter(Objects::nonNull)
                .filter(en -> en.getValue() > 5)
                .filter(en->en.getKey().length()>1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        numMap.entrySet()
                .parallelStream()
                .map(Map.Entry::getKey)
                .forEach(dicMap::make);

        List<String> dic_num =
                numMap.entrySet()
                .parallelStream()
                .map(en -> en.getKey() + " " + en.getValue())
                        .collect(Collectors.toList());

        FileUtils.writeLines(new File("data/_dic_num_.txt"), "UTF-8", dic_num);
        long end = System.currentTimeMillis();
        System.out.println("cost time : " + (end - start));
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
