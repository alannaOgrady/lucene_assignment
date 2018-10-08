package com.alannaogrady;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.RAMDirectory;


public class App 
{
    public static void main(String[] args) throws IOException, ParseException, QueryNodeException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();
        MyIndexWriter iw = MyIndexWriter.getInstance();
        MyIndexSearcher searcher = MyIndexSearcher.getInstance();
        searcher.queryFileParser();

        //Directory index = MyIndexWriter.getInstance().index();
        for (int i = 0; i < 2; i++) {
            Directory index = iw.index(i);
            String fileName = "trec_res_" + iw.getConfig().getSimilarity().toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter("../lucene_assignment/results/" + fileName));
            searcher.search(iw.getConfig(), index, writer);
            writer.close();
        }

    }

}
