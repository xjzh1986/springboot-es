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
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
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
@Api(tags = "博客其他模块")
public class BlogOthreController {
    @Autowired
    TransportClient client;

    @ApiOperation(value = "根据条件删除博客", notes = "根据条件删除博客")
    @PostMapping(value = "/delByTitleAndAut")
    public ResponseEntity delByTitleAndAut(@RequestBody Blog blog) {
        try {
            BulkByScrollResponse response =
                    new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
                            .filter(QueryBuilders.matchQuery("title", blog.getTitle()))
                            .filter(QueryBuilders.matchQuery("author", blog.getAuthro()))
                            .source("book")
                            .get();
            long deleted = response.getDeleted();
            return new ResponseEntity(deleted, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据条件模糊匹配删除博客", notes = "根据条件模糊匹配删除博客")
    @PostMapping(value = "/delLikeByTitle")
    public ResponseEntity delLikeByTitle(@RequestBody Blog blog) {
        try {
            BulkByScrollResponse response =
                    new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
                            .filter(QueryBuilders.wildcardQuery("title", "*"+blog.getTitle()+"*"))
                            .source("book")
                            .get();
            long deleted = response.getDeleted();
            return new ResponseEntity(deleted, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
