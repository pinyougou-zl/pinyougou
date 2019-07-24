var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{status:'0'}  //在这添加一个status，只显示未审核的商家
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/seller/search?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/seller/findAll').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/seller/findPage',{params:{
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
            axios.post('/seller/add',this.entity).then(function (response) {
                if(response.data.success){
                    alert(response.data.message);
                    //跳转到登录的页面
                    window.location.href="shoplogin.html";
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/seller/update',this.entity).then(function (response) {
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
            //alert(id);
            axios.get('/seller/findOne/'+id).then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/seller/delete',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //审核商家的方法
        updateStatus:function (id,status) {
            var params = new URLSearchParams();
            params.append("id",id);
            params.append("status",status);
            axios.post('/seller/updateStatus',params).then(function (response) {
                if(response.data.success) {
                    //修改成功
                    app.searchEntity={};
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
    }

})
