app.controller("basecontroller",function ($scope) {

    //重新加载列表 数据
    $scope.reloadList=function () {
                                    //代表当前页                               //代表总页数
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)

    }
    //分页控件配置。
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };

    //全选删除
    $scope.selectIds=[];
    $scope.selectAll=function ($event) {
        $scope.selectIds=[];//清空被选Id
        var isAllChecked=$event.target.checked;

        if(isAllChecked){
            for (var i=0;i< $scope.list.length;i++){
                $scope.selectlds.push($scope.list[i].id)
            }
        }
    }

    $scope.updateSelection=function ($event,id) {
        if ($event.target.checked) {
            $scope.selectlds.push(id)
        } else {
            var index = $scope.selectlds.indexOf(id)//查找值得位置
            $scope.selectIds.splice(index, 1);//splice移除 参数1：移除得位置  参数2：移除得个数
        }
    }

    $scope.jsonToString=function (jsonString,key) {
            var json=JSON.parse(jsonString)

            var value="";

            for (var i=0;i<json.length;i++){
                if(i>0){
                    value += ",";
                }
                value = json[i][key]
            }
            return value;
    }
    //从集合中按照key查询对象
    $scope.searchObjectByKey=function (list,key,keyValue) {
        for (var i=0; i<list.length;i++){
            if (list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }

});