package com.atguigu.gmall.serach.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;
import com.atguigu.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SerachServiceImpl
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-13
 * @Description:
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    JestClient jestClient;

    //通过catalog3Id检索数据
    @Override
    public List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam) {
        //dsl语句
        String myDsl = getDsl(pmsSearchParam);
        //查询命令对象
        Search search = new Search.Builder(myDsl).addIndex("gmall").addType("pmsSearchSkuInfo").build();
        SearchResult executeResult =null;
        try {
            executeResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //解析es的结果集
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        List<SearchResult.Hit<PmsSearchSkuInfo,Void>> hits = executeResult.getHits(PmsSearchSkuInfo.class);
        String keyword = pmsSearchParam.getKeyword();
        //当keyword为空的时候就没有高亮字段
        if(StringUtils.isNotBlank(keyword)) {
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;//这条数据的高亮字符
                String high = highlight.get("skuName").get(0);
                source.setSkuName(high);
                pmsSearchSkuInfos.add(source);
            }
        }else{
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo source = hit.source;
                pmsSearchSkuInfos.add(source);
            }
        }

        return pmsSearchSkuInfos;
    }

    //获取dsl语句
    private String getDsl(PmsSearchParam pmsSearchParam) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] valueIds = pmsSearchParam.getValueId();
        //query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //如果三级分类id不为空
        if(StringUtils.isNotBlank(catalog3Id)){
            //term
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",catalog3Id);
            //filter
            boolQueryBuilder.filter(termQueryBuilder);
        }
        //如果keyWord不为空
        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            //must
            boolQueryBuilder.must(matchQueryBuilder);
            //高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuName");//需要高亮的字段
            highlightBuilder.preTags("<span style='font-weight:bolder;bolder;color:red'>");//自定义高亮显示样式，前缀
            highlightBuilder.postTags("</span>");//后缀
            searchSourceBuilder.highlight(highlightBuilder);
        }
        //如果valueId不为空
        if(valueIds != null && valueIds.length > 0){
           TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("skuAttrValueList.valueId",valueIds);
           boolQueryBuilder.filter(termsQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        return searchSourceBuilder.toString();
    }
}
