app.controller("baseController",function ($scope) {

    //初始化分页参数
    $scope.paginationConf = {
        currentPage:1 , //默认查询第一页
        totalItems:0,//总记录数
        itemsPerPage:10,//页大小
        perPageOptions:[10,20,30],//可选择的每页大小
        /*onChange:function () {//当上述的参数发生变化了后触发
            $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        }*/
        onChange:function () {
            $scope.reloadList();
        }
    };

    $scope.reloadList = function(){
        //$scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //定义一个放置选择了 id 的数组
    $scope.selectedIds = [];

    //复选框的点击事件
    $scope.updateSelection = function ($event,id) {
        if ($event.target.checked){
            //将选中的选项id添加至数组中
            $scope.selectedIds.push(id);
        }else {
            //反选则从id数组中删除
            var index = $scope.selectedIds.indexOf(id);
            //删除指定位置的id
            $scope.selectedIds.splice(index,1);
        }
    };

    $scope.jsonToString = function (jsonStr,key) {

        var str = "";
        var jsonArray = JSON.parse(jsonStr);
        for (var i = 0; i < jsonArray.length; i++){
            if (i > 0) {
                str +=",";
            }
            str += jsonArray[i][key];
        }
        return str;

    };
});