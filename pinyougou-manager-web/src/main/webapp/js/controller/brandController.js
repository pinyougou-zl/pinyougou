var app = new Vue({
    el: "#app",
    data: {
        pages: 15,//总页数 初始化值为15
        pageNo: 1,//当前页 初始值 1
        entity: {},//品牌对象
        searchEntity: {},//搜索条件对象
        ids: [],//存储要删除的品牌的id列表
        list: []//数组  [{id,name,firstchar},{},{}]
    },
    methods: {
        findAll: function () {
            //发送请求 获取列表数据 赋值给变量
            axios.get('/brand/findAll').then(
                function (response) {//response.data= list
                    app.list = response.data;
                });
        },

        //分页代码
        /*searchList:function (curPage) {
            axios.post('/brand/findPage?pageNo='+curPage).then(function (response) {
                //获取数据
                app.list = response.data.list;
                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },*/

        searchList:function(curPage) {
          axios.post('/brand/search?pageNo='+curPage,this.searchEntity).then(function (response) {
              app.pageNo = response.data.pageNum;
              app.pages = response.data.pages;
              app.list = response.data.list;
          })
        },


        //增加品牌代码
        add:function () {
            axios.post('/brand/add',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (erro) {
                console.log("出错")
            })
        },

        //回显，一点修改就会显示出品牌信息
        findOne:function (id) {
            axios.post('/brand/findOne/'+id).then(function (response) {
                app.entity = response.data;
            })
        },

        //修改品牌
        update:function () {
            axios.post('/brand/update',this.entity).then(function (response) {
                if(response.data.success) {
                    //刷新页面
                    app.searchList(1);
                }
            })
        },

        //由于这个保存按钮都要用到插入跟更新，需要判断
        save:function () {
            //进行判定就是查看id有没有值
            if(this.entity.id!=null) {
                //id有值执行更新方法
                this.update();
            }else {
                this.add();
            }
        },

        //删除方法
        dele:function () {
            axios.post('/brand/delete',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("删除失败");
            })
        }
    },
    //钩子函数
    /*created: function () {
        this.findAll();
    },*/

    created:function () {
        this.searchList(1)
    }
})
