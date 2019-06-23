app.service("brandService",function ($http) {
    //读取列表数据绑定到表单中
    this.findAll=function () {
        return $http.get('../brand/findAll.do');
    }
    //分页
    this.findPage=function (page,rows) {
        return $http.get("../brand/findPage.do?page="+page +"&rows="+rows)
    }
    //查询实体
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id="+id)
    }
    //增加
    this.findAdd=function (entity) {
        return $http.post("../brand/findAdd.do",entity)
    }
    //修改
    this.update=function (entity) {
        return $http.post("../brand/update.do",entity)
    }
    //删除
    this.dele=function (idss) {
        return $http.get("../brand/delects.do?id="+idss)
    }
    //搜索
    this.search=function (page,rows,y) {
        return $http.post("../brand/search.do?page="+page +"&rows="+rows,y)
    }

    this.selectOptionList=function(){
        return $http.get('../brand/selectOptionList.do');
    }

})