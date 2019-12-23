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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/blogOther")
@Api(tags = "博客模块")
public class BlogOthreController {
    @Autowired
    TransportClient client;

    @ApiOperation(value = "根据参数添加博客", notes = "根据参数添加博客")
    @PostMapping(value = "/add")
    public ResponseEntity add(@RequestBody Blog blog) {
        try {
            IndexResponse result = null;
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

}
