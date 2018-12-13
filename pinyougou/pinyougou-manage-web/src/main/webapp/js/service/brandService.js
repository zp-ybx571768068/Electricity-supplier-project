//定义业务对象
app.service("brandService",function ($http) {

    //查询所有
    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    };
    //新增
    this.add = function (entity) {
        return $http.post("../brand/add.do", entity);
    };
    //修改
    this.update = function (entity) {
        return $http.post("../brand/update.do", entity);
    };

    //根据主键查询
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    };

    //删除
    this.delete = function () {
       return $http.get("../brand/delete.do?ids=" + selectedIds);
    };

    //搜索
    this.search = function (searchEntity,page, rows) {
        return $http.post("../brand/search.do?page=" + page + "&rows=" + rows,searchEntity);
    };
});