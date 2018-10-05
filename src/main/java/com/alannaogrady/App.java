package com.alannaogrady;

// java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.RAMDirectory;

import com.alannaogrady.MyIndexWriter;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();
        MyIndexWriter iw = MyIndexWriter.getInstance();
        MyIndexSearcher searcher = MyIndexSearcher.getInstance();
        searcher.queryFileParser();

        //Directory index = MyIndexWriter.getInstance().index();
        for (int i = 0; i < 2; i++) {
            Directory index = iw.index(i);
            searcher.search(iw.getConfig(), index);
        }
        //MyIndexSearcher.getInstance().search(index);
        // 2. query
        //String querystr = args.length > 0 ? args[0] : "lucene";
        //hard coding query for now
        /*String querystr = "what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft .";


        *//*TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(querystr));
        CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
        tokenStream.reset();

        List<String> tokens = new ArrayList(); while (tokenStream.incrementToken()) { tokens.add(termAttribute.toString()); }

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        for(String token : tokens) {
            booleanQuery.add(new PrefixQuery(new Term("content", token)),  BooleanClause.Occur.MUST);
        }
        tokenStream.close();*//*

        //boolean query!!!!!!!!
        //term query!!!!!!
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser("content", analyzer).parse(querystr);

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        TopDocs docs = searcher.search(q, hitsPerPage);
        //TopDocs docs = searcher.search(booleanQuery.build(), hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;   //returns an array of retrieved documents

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            Similarity bm25 = searcher.getSimilarity(true);

            System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title") + " " + bm25.toString());
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();*/
    }

}
