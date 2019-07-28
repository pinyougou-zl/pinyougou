var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{parentId:0},
        ids:[],
        entity_1:{},//变量1
        entity_2:{},//变量2
        grade:1,//当前等级
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/itemCat/search?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/itemCat/findAll').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/itemCat/findPage',{params:{
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
            axios.post('/itemCat/add',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/itemCat/update',this.entity).then(function (response) {
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
            axios.get('/itemCat/findOne/'+id).then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/itemCat/delete',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //查询方法
        findByParentId:function (parentId) {
            axios.get('/itemCat/findByParentId/'+parentId).then(function (response) {
                app.list = response.data;
                //记录上一级的parentId,保存的时候，用到此变量entity里面就有了
                app.entity.parentId = parentId;
            }).catch(function (error) {
                console.log("132123123")
            })
        },
        selectList:function (p_entity) {
            //如果等级为1
            if(this.grade == 1) {
                this.entity_1={};
                this.entity_2={};
            }
            if(this.grade == 2) {
                this.entity_1 = p_entity;
                this.entity_2 = {};
            }
            if(this.grade == 3) {
                this.entity_2 = p_entity;
            }
            this.findByParentId(p_entity.id);
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
        //this.findByParentId(0);
        this.selectList({id:0});
        this.searchList(1);

    }

})
