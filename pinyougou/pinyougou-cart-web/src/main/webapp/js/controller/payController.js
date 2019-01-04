app.controller("payController",function ($scope,cartService) {
    /**
     *  获取当前用户的用户名
     */
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username=response.username;
        })
    };
});