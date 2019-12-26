package com.example.demo.controller;


import com.example.demo.constant.BookConstant;
import com.example.demo.entity.Blog;
import com.example.demo.entity.Book;
import com.example.demo.utils.DateLogUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value = "/blog")
@Api(tags = "书目模块")
public class BookController {
    @Autowired
    TransportClient client;

    @ApiOperation(value = "根据参数添加书目", notes = "根据参数添加书目")
    @PostMapping(value = "/add")
    public ResponseEntity add(@RequestBody Book book) {
        Date startDate = DateLogUtils.startDateLog();
        try {
            IndexResponse result = null;
            XContentBuilder content = XContentFactory.jsonBuilder().startObject()
                    .field(BookConstant.title, book.getTitle())
                    .field(BookConstant.authro, book.getAuthro())
//                    .field(BookConstant.wordCount, (int)(Math.random()*(9999-1000+1)+1000))
                    .field(BookConstant.wordCount, book.getWordCount())
                    .field(BookConstant.publishDate, book.getPublishDate())
                    .field(BookConstant.blogType, book.getBlogType())
                    .field(BookConstant.blogOrder, book.getBlogOrder())
                    .field(BookConstant.content, book.getContent())
                    .field(BookConstant.frontImage, book.getFrontImage())
                    .endObject();
            result = this.client.prepareIndex(BookConstant.blogIndex, BookConstant.blogTypeNoval).setSource(content).get();
            DateLogUtils.endDateLog(startDate);
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据测试数据批量添加书目", notes = "根据测试数据批量添加书目")
    @PostMapping(value = "/addBatch")
    public ResponseEntity addBatch() {
        String[] types = new String[4];
        types[0] = "TECHNOLOGY";
        types[1] = "HUMANITY";
        types[2] = "HEART";
        types[3] = "CHEMICAL";
        try {
            IndexResponse result = null;
            Date startDate = DateLogUtils.startDateLog();
            //批量添加
            int num = 0;
            for(int i=0;i<10;i++){
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
                        .field("typeEs", types[num])
                        .endObject();
                System.out.println("成功插入 第 " + i + " 条");
                result = this.client.prepareIndex("book2", "novel").setSource(content).get();
                if(num < 2){
                    num++;
                }else{
                    num = 0;
                }
            }
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
