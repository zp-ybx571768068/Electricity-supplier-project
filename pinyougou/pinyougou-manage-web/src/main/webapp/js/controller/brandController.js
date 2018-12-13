app.controller("brandController",function ($scope,$http,$controller, brandService) {

    //继承一个controller
    $controller("baseController",{$scope:$scope});

    // //查询所有列表list数据
    //查询所有
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        }).error(function () {
            alert("加载数据失败！");
        });
    };



    //根据分页信息查询
    $scope.findPage = function (page, rows) {
        brandService.findPage(page,rows).success(function (response) {
            //response 分页结果对象total,rows
            $scope.list = response.rows;
            //总记录数
            $scope.paginationConf.totalItems = response.total;

        });
    };

    //保存
    $scope.save = function() {

        var obj;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }

        obj.success(function (response) {
            if (response.success) {
                //重新加载列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    };

    $scope.findOne = function (){
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    };

    //批量删除
    $scope.delete = function(){

        if ($scope.selectIds.length == 0){
            alert("请选择要删除的记录");
            return;
        }
        if (confirm("确定要删除选中的记录吗")){
            brandService.delete($scope.selectedIds).success(function
                (response) {
                if (response.success) {
                    $scope.reloadList();
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            });
        }
    };


    //搜索
    //定义一个空的搜索对象
    $scope.searchEntity={};

    $scope.search = function (page,rows) {

        brandService.search($scope.searchEntity,page,rows).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };
});