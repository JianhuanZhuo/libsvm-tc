package cn.keepfight.tc;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 字典映射
 * Created by 卓建欢 on 2017/12/21.
 */
public class DicMap {
    public static final String ENCODE = "UTF-8";
    public static final String FILE_DEFAULT = "data/_dic_map_.txt";
    public static final String STOP_DEFAULT = "data/_stop_words_.txt";

    private AtomicInteger count = new AtomicInteger(0);
    private Set<String> stopWords;
//    private HashMap<String, Integer> map = new HashMap<>();
    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();


    private static DicMap instance;

    public static DicMap getInstance() {
        if (instance == null) {
            instance = new DicMap();
        }
        return instance;
    }

    public DicMap() {
        this(new File(STOP_DEFAULT));
    }

    public DicMap(File file) {
        try {
            stopWords = new HashSet<>(FileUtils.readLines(file, ENCODE));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("stop words can't be found!");
            System.exit(-2);
        }
    }

    public int make(String word) {
        if (stopWords.contains(word)) {
            return -1;
        }
        return getMap().computeIfAbsent(word, k -> count.incrementAndGet());
    }

    public void read() throws IOException {
        read(new File(FILE_DEFAULT));
    }

    public void read(File file) throws IOException {
        map = new ConcurrentHashMap<>();
        try {
            FileUtils.readLines(file, ENCODE)
                    .forEach(line -> {
                        try {
                            String[] sp = line.split(" ");
                            int num = Integer.parseInt(sp[0]);
                            String word = sp[1];
                            map.put(word, num);
                        } catch (Exception e) {
                            System.err.println("read error : " + line);
                        }
                    });
        } finally {
            refreshCount();
        }
    }

    public void write() throws IOException {
        write(new File(FILE_DEFAULT));
    }

    public Map<String, Integer> getMap() {
//        if (map==null){
////            try {
////                read();
////            } catch (IOException e) {
////                e.printStackTrace();
////                System.err.println("no map and I can't read it with default file");
////                System.exit(-1);
////            }
//            map=new HashMap<>();
//        }
        return map;
    }

    /**
     * 写字典到文件中
     */
    public void write(File file) throws IOException {
        FileUtils.writeLines(file,
                ENCODE,
                map.entrySet().stream()
                        .sorted(Comparator.comparing(Map.Entry::getValue))
                        .map(e -> (e.getValue() + " " + e.getKey()))
                        .collect(Collectors.toList())
        );
    }

    public Integer get(String s){
        return getMap().get(s);
    }

    private void refreshCount() {
        count.set(getMap().values().stream().mapToInt(x -> x).max().orElse(0));
    }
}
