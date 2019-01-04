app.controller("cartController",function ($scope,cartService) {

    /**
     *  增减删除购物车数据
     */
    $scope.addItemToCartList = function(itemId,num){
        cartService.addItemToCartList(itemId,num).success(function (response){
            if (response.success){
                $scope.findCartList();
            }else {
                alert(response.message);
            }
        });
    };
    /**
     *  获取购物车列表数据
     */
    $scope.findCartList= function(){
        cartService.findCartList().success(function (response) {
         $scope.cartList = response;
         //计算购买总数和总价
         $scope.totalValue = cartService.sumTotalValue(response);
        })
    };

    /**
     *  获取当前用户的用户名
     */
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username=response.username;
        })
    };
});