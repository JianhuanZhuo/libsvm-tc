package cn.keepfight.tc;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 卓建欢 on 2017/12/21.
 */
public class LabeledProcessor {
    public static final String LABLED_FILE = "data/labeled.txt";

    public List<String> getLabeledText() throws IOException {
        return getLabeled().stream().map(Pair::getV).collect(Collectors.toList());
    }

    public List<Pair<Integer, String>> getLabeled() throws IOException {
        return FileUtils.readLines(new File(LABLED_FILE), "UTF-8")
                .stream()
                .limit(10000)
                .map(s -> new Pair<>(Integer.parseInt(s.substring(0, 1)), s.substring(2)))
                .collect(Collectors.toList());
//        return getTested();
    }


    public List<Pair<Integer, String>> getTested() throws IOException {
        List<Pair<Integer, String>> list = FileUtils.readLines(new File(LABLED_FILE), "UTF-8")
                .stream()
                .map(s -> new Pair<>(Integer.parseInt(s.substring(0, 1)), s.substring(2)))
                .sorted(Comparator.comparing(Pair::getK))
                .collect(Collectors.toList());
        List<Pair<Integer, String>> positiveList = list.stream()
                .filter(pr -> pr.getK() == 0)
                .limit(40000)
                .collect(Collectors.toList());

        List<Pair<Integer, String>> negtiveList = list.stream()
                .filter(pr -> pr.getK() == 1)
                .limit(10000)
                .collect(Collectors.toList());
        positiveList.addAll(negtiveList);
        return positiveList;
    }


    public List<String> getTestedText() throws IOException {
        return getTested().stream().map(Pair::getV).collect(Collectors.toList());
    }
}
