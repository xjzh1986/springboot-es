package com.example.demo.controller;

import com.example.demo.constant.BookConstant;
import com.example.demo.utils.DateLogUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/blogSearch")
@Api(tags = "图书查询模块")
public class BookSearchController {

    @Autowired
    TransportClient client;

    @ApiOperation(value = "根据条件查询图书", notes = "根据条件查询图书")
    @PostMapping("/queryByParam")
    public ResponseEntity queryByParam(@RequestParam(name = "author", required = false) String author,
                                       @RequestParam(name = "title", required = false) String title,
                                       @RequestParam(name = "gt_word_count", defaultValue = "0") int gtWordCount,
                                       @RequestParam(name = "lt_word_count", required = false) Integer ltWordCount)
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (author != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery(BookConstant.authro,author));
        }

        if (title != null){
            boolQueryBuilder.must(QueryBuilders.wildcardQuery(BookConstant.title, "*"+title+"*"));
        }

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(BookConstant.wordCount).from(gtWordCount);
        if (ltWordCount != null && ltWordCount > 0){
            rangeQueryBuilder.to(ltWordCount);
            boolQueryBuilder.filter(rangeQueryBuilder);
        }


        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(BookConstant.blogIndex)
                .setTypes(BookConstant.blogTypeNoval)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(10)
                .addSort(BookConstant.wordCount, SortOrder.DESC);
        System.out.println(searchRequestBuilder); //调试用
        SearchResponse response = searchRequestBuilder.get();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : response.getHits()){
            String tt = hit.getId();
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id",hit.getId());
            result.add(map);
        }
        return  new ResponseEntity(result, HttpStatus.OK);
    }

    @ApiOperation(value = "根据条件分页查询图书", notes = "根据条件分页查询图书")
    @PostMapping("/queryPageByParam")
    public ResponseEntity queryPageByParam(@RequestParam(name = "author", required = false) String author,
                                           @RequestParam(name = "title", required = false) String title,
                                           @RequestParam(name = "gt_word_count", defaultValue = "0") int gtWordCount,
                                           @RequestParam(name = "lt_word_count", required = false) Integer ltWordCount){

        Date startDate = DateLogUtils.startDateLog();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (author != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery(BookConstant.authro,author));
        }
        if (title != null){
            boolQueryBuilder.must(QueryBuilders.wildcardQuery(BookConstant.title, "*"+title+"*"));
        }

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(BookConstant.wordCount).from(gtWordCount);
        if (ltWordCount != null && ltWordCount > 0){
            rangeQueryBuilder.to(ltWordCount);
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(BookConstant.blogIndex)
                .setTypes(BookConstant.blogTypeNoval)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(10)
                .addSort(BookConstant.publishDate, SortOrder.DESC);

        SearchResponse response = searchRequestBuilder.get();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : response.getHits()){
            String tt = hit.getId();
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id",hit.getId());
            result.add(map);
        }

        long searchRequestBuilderTotal = this.client.prepareSearch(BookConstant.blogIndex)
                .setTypes(BookConstant.blogTypeNoval)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder).get().getHits().getTotalHits();
        System.out.println("searchRequestBuilderTotal" + searchRequestBuilderTotal);

        DateLogUtils.endDateLog(startDate);
        return  new ResponseEntity(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Stats 聚合操作 count min max avg sum", notes = "Stats 聚合操作 count min max avg sum")
    @PostMapping(value = "/queryStatsOpt")
    public ResponseEntity queryByPage(
            @RequestParam(name = "title", required = false) String title) {
        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            if (title != null){
                boolQueryBuilder.must(QueryBuilders.wildcardQuery(BookConstant.title, "*"+title+"*"));
            }

            SearchRequestBuilder search = client.prepareSearch(BookConstant.blogIndex)
                    .setTypes(BookConstant.blogTypeNoval)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder);

//            SearchResponse sr =  search.addAggregation(AggregationBuilders.stats("wordCount_stats").field("wordCount")).execute().actionGet();
//            Stats stats = sr.getAggregations().get("wordCount_stats");
//            System.out.println(stats.getAvgAsString());
//            System.out.println(stats.getMaxAsString());
//            System.out.println(stats.getMinAsString());
//            System.out.println(stats.getSumAsString());
//            System.out.println(stats.getCount());

//            SearchResponse sr =  search.addAggregation(AggregationBuilders.stats("typeEs_stats").field("typeEs.keyword")).execute().actionGet();
            SearchResponse sr =  search.addAggregation(AggregationBuilders.terms(BookConstant.blogType+"_stats").field(BookConstant.blogType+".keyword")).execute().actionGet();
            Terms terms = sr.getAggregations().get(BookConstant.blogType+"_stats");
            for(int i=0;i<terms.getBuckets().size();i++){
                //statistics
                String id =terms.getBuckets().get(i).getKey().toString();//id
                Long sum =terms.getBuckets().get(i).getDocCount();//数量
                System.out.println("=="+terms.getBuckets().get(i).getDocCount()+"------"+terms.getBuckets().get(i).getKey());
            }


            return new ResponseEntity(0, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
