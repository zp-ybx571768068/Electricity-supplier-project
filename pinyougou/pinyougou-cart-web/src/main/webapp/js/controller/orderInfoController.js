app.controller("orderInfoController",function ($scope,cartService,addressService) {





    //支付方式；默认为微信付款
    $scope.order = {"paymentType":"1"};
    /**
     * 选择支付类型
     */
    $scope.selectPayType = function(type){
        alert(type);
        $scope.order.paymentType = type;
    };

    /**
     *  提交订单
     */
    $scope.submitOrder =function(){
        //设置用户收货地址
        $scope.order.receiverAreaName = $scope.address.address;
        //设置用户手机号
        $scope.order.receiverMobile = $scope.address.mobile;
        //设置用户收件人名
        $scope.order.receiver = $scope.address.contact;
        //调用cartService保存订单
        cartService.submitOrder($scope.order).success(function (response) {
            //保存成功
            if (response.success){
                //如果支付方式是微信支付
                if ($scope.order.paymentType == "1"){
                    //携带支付业务id，跳转至支付页面
                    location.href = "pay.html#?outTradeNo="+response.message;
                } else {
                    //支付方式是货到付款直接跳转付款成功页面
                    location.href = "paysuccess.html";
                }
            }else {
                //保存失败,提示错误信息
                alert(response.message);
            }
        })
    } ;

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