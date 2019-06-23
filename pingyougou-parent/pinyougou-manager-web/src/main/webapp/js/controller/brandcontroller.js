app.controller("brandcontroller",function ($scope,$controller,brandService) {


    $controller('basecontroller',{$scope:$scope});//继承

    //读取列表数据绑定到表单中

    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }
    //分页
    $scope.findPage=function (page,rows) {
        brandService.findPage(page,rows).success(

            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        )

    }
    //查询实体
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
                $scope.reloadList();
            }
        )
    }
    //保存
    $scope.findAdd=function () {
        var serviceObject;
        if($scope.entity.id!=null){
            serviceObject=brandService.update($scope.entity)
        }else {
            serviceObject=brandService.findAdd($scope.entity)
        }
        serviceObject.success(
            function (response) {
                if (response.success){
                    //为ture 就重新加载查询页面
                    $scope.reloadList()
                }else {
                    alert(response.message);
                }
            }
        )
    }

    //批量删除
    $scope.dele=function () {

        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success){
                    //为ture 就重新加载查询页面
                    $scope.reloadList()
                }else {
                    alert(response.message);
                }
            }
        )
    }
    $scope.y={}  //定义搜索对象
    //查询
    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.y).success(

            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        )

    }
})