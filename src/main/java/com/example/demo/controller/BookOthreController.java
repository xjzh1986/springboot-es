package com.example.demo.controller;


import com.example.demo.constant.BookConstant;
import com.example.demo.entity.Book;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.*;
import org.elasticsearch.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/blogOther")
@Api(tags = "图书参数匹配模块")
public class BookOthreController {
    @Autowired
    TransportClient client;

    @ApiOperation(value = "根据条件删除图书", notes = "根据条件删除图书")
    @PostMapping(value = "/delByTitleAndAut")
    public ResponseEntity delByTitleAndAut(@RequestBody Book book) {
        try {
            BulkByScrollResponse response =
                    new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
                            .filter(QueryBuilders.matchQuery(BookConstant.title, book.getTitle()))
                            .filter(QueryBuilders.matchQuery(BookConstant.authro, book.getAuthro()))
                            .source(BookConstant.blogIndex)
                            .get();
            long deleted = response.getDeleted();
            return new ResponseEntity(deleted, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据条件模糊匹配删除图书", notes = "根据条件模糊匹配删除图书")
    @PostMapping(value = "/delLikeByTitle")
    public ResponseEntity delLikeByTitle(@RequestBody Book book) {
        try {
            BulkByScrollResponse response =
                    new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
                            .filter(QueryBuilders.wildcardQuery(BookConstant.title, "*"+book.getTitle()+"*"))
                            .source(BookConstant.blogIndex)
                            .get();
            long deleted = response.getDeleted();
            return new ResponseEntity(deleted, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据条件匹配更新图书", notes = "根据条件匹配更新图书")
    @PostMapping(value = "/modifyByTitle")
    public ResponseEntity modifyByTitle1(@RequestBody Book book) {
        try {
            UpdateByQueryRequestBuilder updateByQueryRequestBuilder = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            updateByQueryRequestBuilder.source(BookConstant.blogIndex);
            updateByQueryRequestBuilder.filter(QueryBuilders.termQuery(BookConstant.title, book.getTitle()));
            updateByQueryRequestBuilder.script(
                    new Script("ctx._source.authro="+book.getAuthro()
                            +";ctx._source.wordCount="+book.getWordCount()
                            +";"));
            long count =updateByQueryRequestBuilder.get().getUpdated();
            return new ResponseEntity(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据条件模糊匹配更新图书", notes = "根据条件模糊匹配更新图书")
    @PostMapping(value = "/modifyLikeByTitle")
    public ResponseEntity modifyLikeByTitle1(@RequestBody Book book) {
        try {
            UpdateByQueryRequestBuilder updateByQueryRequestBuilder = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
            updateByQueryRequestBuilder.source(BookConstant.blogIndex);
            updateByQueryRequestBuilder.filter(QueryBuilders.wildcardQuery(BookConstant.title, "*"+book.getTitle()+"*"));
            updateByQueryRequestBuilder.script(
                    new Script("ctx._source.authro="+book.getAuthro()
                            +";ctx._source.wordCount="+book.getWordCount()
                            +";"));
            long count =updateByQueryRequestBuilder.get().getUpdated();
            return new ResponseEntity(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
