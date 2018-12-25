app.controller("searchController", function ($scope, searchService) {

    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            //构建页面分页导航条信息
            buildPageInfo();
        });
    };

    //搜索条件对象
    $scope.searchMap = {"keywords":"","category":"","brand":"","spec":{},"price":"","pageNo":1,"pageSize":40};

    //过滤查询
    $scope.addSearchItem = function(key,value){
        if ("brand" == key || "category" == key || "price" == key){
            //如果点击的是品牌或者分类
            $scope.searchMap[key] = value;
        } else {
            //如果是规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.searchMap.pageNo=1;
        //点击过滤条件之后需要重新搜索
        $scope.search();
    };
    //撤销过滤条件
    $scope.removeSearchItem = function(key){
        if ("brand" == key || "category" == key || "price" == key){
            //如果点击的是品牌或者分类
            $scope.searchMap[key] ='';
        } else {
            //如果是规格
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo=1;
        //点击过滤条件之后需要重新搜索
        $scope.search();
    };

    //构建页面分页导航条信息
    buildPageInfo = function () {
        //定义要再页面显示的页号集合
        $scope.pageNoList = [];

        //定义要在页面显示的页号数量
        var showPageNoTotal = 5;

        //起始页号
        var startPageNo = 1;
        //结束页号
        var endPageNo = $scope.resultMap.totalPages;

        //如果总页数大于要显示的页数才有需要处理显示页号数；否则直接显示所有页号
        if ($scope.resultMap.totalPages > showPageNoTotal){

            //计算当前页的左右间隔
            var interval = Math.floor(showPageNoTotal / 2);

            //根据间隔得出起始、结束页号
            startPageNo = parseInt($scope.searchMap.pageNo)-interval;
            endPageNo = parseInt($scope.searchMap.pageNo) + interval;

            //处理页号越界
            if (startPageNo >= 1){
                // 如果结束页号是大于总页数的则都设置为总页数，起始页号就
                if (endPageNo > $scope.resultMap.totalPages){
                    startPageNo = $scope.resultMap.totalPages-showPageNoTotal+1;
                    endPageNo = $scope.resultMap.totalPages
                }
            }else {
                // 如果起始页号是小于1的则都设置为1，结束页号就为要显示的
                startPageNo = 1;
                endPageNo = showPageNoTotal;
            }
        }

        //分页导航条上前后三个点
        $scope.frontDot = false;
        if (startPageNo > 1){
            $scope.frontDot = true;
        }

        $scope.backDot = false;
        if (endPageNo > $scope.resultMap.totalPages) {
            $scope.backDot = true;
        }

        //设置要显示的页号
        for (var i = startPageNo;i <= endPageNo;i++){
            $scope.pageNoList.push(i);
        }
        alert($scope.pageNoList)
    };

    //判断是否为当前页
    $scope.isCurrentPage=function (pageNo) {
       return $scope.searchMap.pageNo == pageNo;
    };

    //根据页号查询
    $scope.queryByPageNo = function (pageNo) {
        pageNo = parseInt(pageNo);
        if (pageNo >0 && pageNo <= $scope.resultMap.totalPages) {
            $scope.searchMap.pageNo = pageNo;
            $scope.search();
        }
    }

});