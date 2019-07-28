var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        itemCatList:[],
        status:['未审核','已审核','审核未通过','已关闭'],
        searchEntity:{auditStatus:'0'}
    },
    methods: {
        //商品审核的方法
        updateStatus:function(status) {
            axios.post('/goods/updateStatus/'+status,this.ids).then(function (response) {
                if(response.data.success) {
                    //更新成功，刷新页面
                    app.searchList(1);
                }
            }).catch(function (erroe) {
                console.log("1231312131321");
            })
        },

        //获取商品所有分类信息的列表
        findAllItemCategory:function() {
            //获取商品的所有分类
            axios.post('/itemCat/findAll').then(function (response) {
                for(var i=0;i<response.data.length;i++) {
                    //response.data[i].name;这个就是查询出来品牌对象里的名称
                    app.itemCatList[response.data[i].id] = response.data[i].name;
                }
                //重新手动渲染
                app.$mount("#app");
            }).catch(function (error) {
                console.log("123456")
            })
        },

        //分页查询
        searchList:function (curPage) {
            //如果我们需要条件查询，绑定该事件，会将参数带过去
            axios.post('/goods/search?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;
                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/goods/findAll').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/goods/findPage',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/goods/add',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/goods/update',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/goods/findOne/'+id).then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/goods/delete',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);
        this.findAllItemCategory();

    }

})
