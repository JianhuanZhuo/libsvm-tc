package cn.keepfight.tc;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 卓建欢 on 2017/12/21.
 */
public class LabeledProcessor {
    public static final String LABLED_FILE = "data/labeled.txt";

    public List<String> getLabeledText() throws IOException {
        return FileUtils.readLines(new File(LABLED_FILE), "UTF-8")
                .stream()
//                .limit(100000)
                .map(s -> s.substring(2))
                .collect(Collectors.toList());
    }
    public List<Pair<Integer, String>> getLabeled() throws IOException {
        return FileUtils.readLines(new File(LABLED_FILE), "UTF-8")
                .stream()
//                .limit(100000)
                .map(s -> new Pair<>(Integer.parseInt(s.substring(0,1)), s.substring(2)))
                .collect(Collectors.toList());
    }
}
