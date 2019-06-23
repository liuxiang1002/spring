package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    //搜索
    public Map<String, Object> search(Map searchMap);

    /**
     * 导入数据
     * @param list
     */
    public void importList(List list);

    /**
     * 商品删除同步索引数据
     * @param goodsIdList
     */
    public void deleteByGoodsIds(List goodsIdList);
}
