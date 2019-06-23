app.controller("searchController",function ($scope,searchService,$location) {

    $scope.search=function () {
        //在执行查询前，转换为int类型，否则提交到后端有可能变成字符串
        $scope.searchMap.pageNO=parseInt( $scope.searchMap.pageNO);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
                buildPageLabel();//调用
            }
        )
    }
    //定义一个对象
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNO':1,'pageSize':20,'sortField':'','sort':'' };//搜索对象
    $scope.addSearchItem=function(key,value){
        if (key=="category" || key=="brand" ||key=="price"){//如果点击的是分类或者是品牌

            $scope.searchMap[key]=value;

        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();//执行搜索
    }

    //移除复合搜索条件
    $scope.removeSearchItem=function(key){
        if (key=="category" || key=="brand" ||key=="price"){//如果点击的是分类或者是品牌

            $scope.searchMap[key]="";

        }else{
           delete $scope.searchMap.spec[key];//删除
        }
        $scope.search();//执行搜索
    }

    //构建分页标签(totalPages为总页数)
     buildPageLabel=function(){
        $scope.pageLabel=[];//新增分页栏属性
         var maxPageNo=$scope.resultMap.totalPages;//得到最后页码
         var firstPage=1;//开始页码
         var lastPage = maxPageNo;//截止页码

         $scope.firstDot=true;//前面有点
         $scope.lastDot=true;//后边有点

         if ($scope.resultMap.totalPages>5){//总页数大于5，就是显示部分页码

             if ($scope.searchMap.pageNO<=3){//如果当前页小于等于3
                 lastPage=5;//前5页
                 $scope.firstDot=false;//前面没点
             }else if ($scope.searchMap.pageNO>=lastPage-2){//如果当前页大于等于最大页码-2
                 firstPage=maxPageNo-4;//后5页
                 $scope.lastDot=false;//后边无点
             }else {//显示当前页为中心的5页
                 firstPage=$scope.searchMap.pageNO-2;
                 lastPage=$scope.searchMap.pageNO+2;
             }

         }else {
             $scope.firstDot=false;//前面无点
             $scope.lastDot=false;//后边无点

         }
         //循环产生页码标签
         for (var i=firstPage;i<=lastPage;i++){
             $scope.pageLabel.push(i);
         }

     }
    //根据页码查询
    $scope.queryByPage=function (pageNo) {
        //页码验证
        if (pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNO = pageNo;
        $scope.search();
    }
    //判断当前页为第一页
    $scope.isTopPage=function () {
        if ($scope.searchMap.pageNO==1){
            return true;
        }else {
            return false;
        }
    }
    //判断当前是否是最后一页
    $scope.isEndPage=function () {
        if ($scope.searchMap.pageNO==$scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    }
    //设置排序规则
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }


    //如果用户输入的是品牌的关键字，则隐藏品牌列表
    //判断关键字是不是品牌

    $scope.keywordsIsBrand=function () {
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
                return true;
            }

        }
        return false
    }
    //接受前台传回来的参数
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search()
    }
})