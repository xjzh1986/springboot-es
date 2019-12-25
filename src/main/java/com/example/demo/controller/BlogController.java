package com.example.demo.controller;


import com.example.demo.entity.Blog;
import com.example.demo.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/blog")
@Api(tags = "书目模块")
public class BlogController {
    @Autowired
    TransportClient client;

    @ApiOperation(value = "根据参数添加书目", notes = "根据参数添加书目")
    @PostMapping(value = "/add")
    public ResponseEntity add(@RequestBody Blog blog) {
        try {
            IndexResponse result = null;
            Date startDate = new Date();
            System.out.println("开始时间"+DateUtils.dateToStr(startDate));
            //批量添加
            for(int i=0;i<50000000;i++){
                XContentBuilder content = XContentFactory.jsonBuilder().startObject()
                        .field("title", "title-1225"+i)
                        .field("author", "Authro-1225"+i)
                        .field("word_count", (int)(Math.random()*(9999-1000+1)+1000))
                        .field("publish_date", new Date().getTime())
                        .endObject();
                System.out.println("成功插入 第 " + i + " 条");
                result = this.client.prepareIndex("book", "novel").setSource(content).get();
            }
//            XContentBuilder content = XContentFactory.jsonBuilder().startObject()
//                    .field("title", blog.getTitle())
//                    .field("author", blog.getAuthro())
//                    .field("word_count", blog.getWordount())
//                    .field("publish_date", blog.getPublishDate().getTime())
//                    .endObject();
//
//            result = this.client.prepareIndex("book", "novel").setSource(content).get();
            Date endDate = new Date();
            System.out.println("结束时间"+DateUtils.dateToStr(endDate));
            System.out.println(DateUtils.getDistanceTime(startDate,endDate));
            return new ResponseEntity(result.getId(), HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据id删除书目", notes = "根据id删除书目")
    @DeleteMapping(value = "/deleteById")
    public ResponseEntity deleteById(@RequestParam(name = "id") String id)
    {
        DeleteResponse result = client.prepareDelete("book", "novel", id).get();
        return new ResponseEntity(result.getResult().toString(), HttpStatus.OK);
    }

    @ApiOperation(value = "根据id查找书目", notes = "根据id查找书目")
    @GetMapping(value = "/getById")
    public ResponseEntity getById(@RequestParam(name = "id", defaultValue="") String id)
    {
        if (id.isEmpty()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        GetResponse result = this.client.prepareGet("book", "novel", id).get();
        if (!result.isExists()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(result.getSource(), HttpStatus.OK);
    }
    @ApiOperation(value = "根据id更新书目", notes = "根据id更新书目")
    @PutMapping("/updatebyId")
    public ResponseEntity updatebyId(@RequestBody Blog blog,@RequestParam(name = "id") String id){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
            if (blog.getTitle()!= null){
                builder.field("title", blog.getTitle());
            }
            if (blog.getAuthro() != null){
                builder.field("author", blog.getAuthro());
            }
            builder.endObject();
            UpdateRequest updateRequest = new UpdateRequest("book", "novel", id);
            updateRequest.doc(builder);

            UpdateResponse result = client.update(updateRequest).get();

            return new ResponseEntity(result.getResult().toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据条件查询书目", notes = "根据条件查询书目")
    @PostMapping("/queryByParam")
    public ResponseEntity queryByParam(@RequestParam(name = "author", required = false) String author,
                                @RequestParam(name = "title", required = false) String title,
                                @RequestParam(name = "gt_word_count", defaultValue = "0") int gtWordCount,
                                @RequestParam(name = "lt_word_count", required = false) Integer ltWordCount)
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (author != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("author",author));
        }
        if (title != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("word_count").from(gtWordCount);
        if (ltWordCount != null && ltWordCount > 0){
            rangeQueryBuilder.to(ltWordCount);
        }

        boolQueryBuilder.filter(rangeQueryBuilder);

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("book")
                .setTypes("novel")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(10)
                .setSize(20)
                .addSort("publish_date", SortOrder.DESC);
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

    @ApiOperation(value = "根据条件分页查询书目", notes = "根据条件分页查询书目")
    @PostMapping("/queryPageByParam")
    public ResponseEntity queryPageByParam(@RequestParam(name = "author", required = false) String author,
                                       @RequestParam(name = "title", required = false) String title,
                                       @RequestParam(name = "gt_word_count", defaultValue = "0") int gtWordCount,
                                       @RequestParam(name = "lt_word_count", required = false) Integer ltWordCount)
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (author != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("author",author));
        }
        if (title != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("word_count").from(gtWordCount);
        if (ltWordCount != null && ltWordCount > 0){
            rangeQueryBuilder.to(ltWordCount);
        }

        boolQueryBuilder.filter(rangeQueryBuilder);

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("book")
                .setTypes("novel")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(10)
                .setSize(20)
                .addSort("publish_date", SortOrder.DESC);

        SearchResponse response = searchRequestBuilder.get();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : response.getHits()){
            String tt = hit.getId();
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id",hit.getId());
            result.add(map);
        }

        long searchRequestBuilderTotal = this.client.prepareSearch("book")
                .setTypes("novel")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(10)
                .setSize(20)
                .addSort("publish_date", SortOrder.DESC).get().getHits().getTotalHits();


        return  new ResponseEntity(result, HttpStatus.OK);
    }

}
