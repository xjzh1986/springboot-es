package com.example.demo.controller;


import com.example.demo.entity.Blog;
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
@Api(tags = "博客模块")
public class BlogController {
    @Autowired
    TransportClient client;

    @ApiOperation(value = "根据参数添加博客", notes = "根据参数添加博客")
    @PostMapping(value = "/add")
    public ResponseEntity add(@RequestBody Blog blog) {
        try {
            IndexResponse result = null;
            //批量添加
//            for(int i=0;i<1000;i++){
//                XContentBuilder content = XContentFactory.jsonBuilder().startObject()
//                        .field("title", "title"+i)
//                        .field("author", "Authro"+i)
//                        .field("word_count", (int)(Math.random()*(9999-1000+1)+1000))
//                        .field("publish_date", new Date().getTime())
//                        .endObject();
//
//                result = this.client.prepareIndex("book", "novel").setSource(content).get();
//            }
            XContentBuilder content = XContentFactory.jsonBuilder().startObject()
                    .field("title", blog.getTitle())
                    .field("author", blog.getAuthro())
                    .field("word_count", blog.getWordount())
                    .field("publish_date", blog.getPublishDate())
                    .endObject();

            result = this.client.prepareIndex("book", "novel").setSource(content).get();

            return new ResponseEntity(result.getId(), HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据id删除博客", notes = "根据id删除博客")
    @DeleteMapping(value = "/deleteById")
    public ResponseEntity deleteById(@RequestParam(name = "id") String id)
    {
        DeleteResponse result = client.prepareDelete("book", "novel", id).get();
        return new ResponseEntity(result.getResult().toString(), HttpStatus.OK);
    }

    @ApiOperation(value = "根据id查找博客", notes = "根据id查找博客")
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
    @ApiOperation(value = "根据id更新博客", notes = "根据id更新博客")
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

    @GetMapping("/queryByParam")
    @ApiOperation(value = "根据id更新博客", notes = "根据id更新博客")
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
                .setFrom(0)
                .setSize(10);
        System.out.println(searchRequestBuilder); //调试用
        SearchResponse response = searchRequestBuilder.get();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : response.getHits()){
            result.add(hit.getSourceAsMap());
        }

        return  new ResponseEntity(result, HttpStatus.OK);
    }
}
