app.controller("orderInfoController",function ($scope,cartService,addressService) {


    //支付方式；默认为微信付款
    $scope.order = {"paymentType":"1"};
    /**
     * 选择支付类型
     */
    $scope.selectPayType = function(type){
        $scope.order.paymentType = type;
    };
    /**
     *  判断地址是否是用户选中的地址
     */
    $scope.isAddressSelected = function(address){
        if ($scope.address == address){
            return true;
        }
        return false;
    };
    /**
     *  选中地址
     */
    $scope.selectAddress = function(address){
      $scope.address = address;
    };
    /**
     *  查找用户地址列表
     */
    $scope.findAddressList = function(){
        addressService.findAddressList().success(function (response) {
            $scope.addressList = response;

            //是否是默认地址
            for (var i = 0; i < $scope.addressList.length; i++){
                var address = $scope.addressList[i];
                if (address.isDefault == 1) {
                    $scope.address = address;
                    break;
                }
            }
        });
    };
    /**
     *  获取购物车列表数据
     */
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            //计算购买总数和总价格
            $scope.totalValue = cartService.sumTotalValue(response);
        });
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