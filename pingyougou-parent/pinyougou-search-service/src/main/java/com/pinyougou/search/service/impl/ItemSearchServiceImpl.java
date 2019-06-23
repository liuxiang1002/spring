package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        //搜索栏关键字中间的空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));

        Map<String,Object> map=new HashMap<>();
        //高亮
        searchList(searchMap);
        //查询列表
        map.putAll(searchList(searchMap));
        //2.根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //3.查询品牌规格和名称
        String category = (String) searchMap.get("category");
        if (!"".equals(category)){
            map.putAll(searchBrandAndSpecList(category));
        }else {
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品ID"+goodsIdList);
        Query query = new SimpleQuery();
        Criteria criteria =new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
    }

    /**
     * 根据关键字搜索列表
     * @paramkeywords
     * @return
     */
    private Map searchList(Map searchMap){
        Map<String,Object> map=new HashMap<>();

//        Query query = new SimpleQuery();

        HighlightQuery  query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前坠
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项

        //1.1关键字查询.....
        //添加查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2按分类筛选
        if (!"".equals(searchMap.get("category"))){
            Criteria filtercriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFacetQuery(filtercriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按品牌筛选
        if (!"".equals(searchMap.get("brand"))){
            Criteria filtercriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFacetQuery(filtercriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4规格过滤
        if(searchMap.get("spect") != null){
            Map<String,String> spect = (Map<String, String>) searchMap.get("spect");
            for (String key:spect.keySet()) {
                Criteria filtercriteria = new Criteria("item_spect_"+key).is(spect.get(key));
                FilterQuery filterQuery = new SimpleFacetQuery(filtercriteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //1.5按价格筛选
        if (!"".equals(searchMap.get("price"))){
            String[] prices = ((String) searchMap.get("price")).split("-");
            if (!prices[0].equals("0")){//如果区间起点不等于0
                Criteria filtercriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFacetQuery(filtercriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!prices[1].equals("*")){//如果区间终点不等于*
                Criteria filtercriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFacetQuery(filtercriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.6分页查询
        Integer pageNO = (Integer) searchMap.get("pageNO");//提取页码
        if (pageNO == null){
            pageNO=1;//默认第一页
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页记录数
        if (pageSize == null){
            pageSize=20;//默认显示记录数20
        }
        query.setOffset((pageNO-1)*pageSize);//从第几条记录查询
        query.setRows(pageSize);
        //1.7排序ASC正序  DESC倒序

        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if(sortValue!=null && !sortValue.equals("")){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }

        //高亮显示处理.....
//        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);


        for (HighlightEntry<TbItem> h:page.getHighlighted()){//循环高亮入口集合
            TbItem entity = h.getEntity();//获取原实体类
            if (h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                entity.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }


        }

        map.put("rows",page.getContent());
        map.put("totalPages", page.getTotalPages());//返回总页数
        map.put("total", page.getTotalElements());//返回总记录数

        return map;
    }
    /**
     * 查询分类列表
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap){
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();

        //添加查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置分组
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //得到分组页；
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");

        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }

        return list;

    }

    /**
     * 查询品牌和规格列表
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId!=null){
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return  map;

    }

}
