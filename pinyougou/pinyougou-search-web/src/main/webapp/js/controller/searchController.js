app.controller("searchController", function ($scope, searchService) {

    //搜索条件对象
    $scope.searchMap = {"keywords":"","category":"","brand":"","spec":{},"price":""};

    //过滤查询
    $scope.addSearchItem = function(key,value){
        if ("brand" == key || "category" == key){
            //如果点击的是品牌或者分类
            $scope.searchMap[key] = value;
        } else {
            //如果是规格
            $scope.searchMap.spec[key] = value;
        }
        //点击过滤条件之后需要重新搜索
        $scope.search();
    };
    //撤销过滤条件
    $scope.removeSearchItem = function(key){
        if ("brand" == key || "category" == key){
            //如果点击的是品牌或者分类
            $scope.searchMap[key] ='';
        } else {
            //如果是规格
            delete $scope.searchMap.spec[key];
        }
        //点击过滤条件之后需要重新搜索
        $scope.search();
    };
    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;

        });
    };

});