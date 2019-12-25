package com.example.demo.controller;


import com.example.demo.entity.Blog;
import com.example.demo.utils.DateLogUtils;
import com.example.demo.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.lucene.queryparser.classic.QueryParser;
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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/blog")
@Api(tags = "书目模块")
public class BlogController {
    @Autowired
    TransportClient client;

    @ApiOperation(value = "根据参数添加书目", notes = "根据参数添加书目")
    @PostMapping(value = "/add")
    public ResponseEntity add(@RequestBody Blog blog) {

        String[] types = new String[4];
        types[0] = "technology";
        types[1] = "humanity";
        types[2] = "heart";
        types[3] = "chemical";

        try {
            IndexResponse result = null;
            Date startDate = DateLogUtils.startDateLog();
            //批量添加
            int num = 0;
            for(int i=0;i<50000000;i++){

                if(num < 2){
                    num++;
                }else{
                    num = 0;
                }

                String code = UUID.randomUUID().toString().replace("-","");
                XContentBuilder content = XContentFactory.jsonBuilder().startObject()
                        .field("title", "title"+i)
                        .field("author", "author"+i)
                        .field("wordCount", (int)(Math.random()*(9999-1000+1)+1000))
                        .field("publishDate", new Date().getTime())
                        .field("createDate", new Date().getTime())
                        .field("updateDate", new Date().getTime())
                        .field("createCode", code)
                        .field("updateCode", code)
                        .field("content", "经常在添加数据到数据库中使用")
                        .field("frontImage", "https://blog.csdn.net/zhengyikuangge/article/details/"+i)
                        .field("type", types[num])
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
            DateLogUtils.endDateLog(startDate);
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



}
