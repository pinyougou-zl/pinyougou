var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{parentId:0},
        ids:[],
        searchEntity:{},
        entity_1:{},//变量1
        entity_2:{},//变量2
        grade:1,//当前等级
        choose:1,//1代表全部分类页，2代表我的申请页
        auditStatus:["待审核","审核通过","驳回","敲你嘛"],// 审核状态
        itemCatList:[]  //所有的分类的数据


    },
    methods: {
        searchList:function (curPage) {
            axios.post('/itemCat/searchOne.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/itemCat/findPage.shtml',{params:{
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
            axios.post('/itemCat/addOne.shtml',this.entity).then(function (response) {
                console.log(response);
                alert(response.data.message);
                if(response.data.success){
                 //   app.searchList(1);
                  //  app.selectList({id:0})
                    app.my(2);
                    app.searchListApply(1);

                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/itemCat/updateOne.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                  //  app.searchList(1);
                   // app.selectList({id:0})

                    app.my(2);
                    app.searchListApply(1);

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

                this.my(2);
                this.searchListApply(1);
            }
        },
        findOne:function (id) {
            axios.get('/itemCat/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/itemCat/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                   // app.searchList(1);
                    app.my(2);
                    app.searchListApply(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findByParentId:function (parentId) {

            axios.get('/itemCat/findByParentIdOne/'+parentId+'.shtml').then(function (response) {
                app.list=response.data;
                //记录下来
                app.entity.parentId=parentId;
            }).catch(function (error) {
                alert("错误");
                console.log("错误");
            })

        },
        selectList:function (p_entity) {
            if (this.grade == 1) {
                this.entity_1={};
                this.entitr_2={};
            }
            if (this.grade == 2) {
                this.entity_1=p_entity;
                this.entity_2={};
            }
            if (this.grade==3){
                this.entity_2=p_entity;
            }
            this.findByParentId(p_entity.id)
        },
        my:function(number){
            this.grade=1;
           this.choose=number;

        },
        searchListApply:function (curPage) {
            axios.post('/itemCat/searchApply.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
               
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;

            });
        },
        //查询所有的分类的数据
        findAllItemCategory:function () {
            axios.get('/itemCat/findAll.shtml').then(
                function (response) {//response.data=[{id:378,name:手机}，{},{}]
                    //itemCatList:[],//格式 是  [null,null,n......,"手机"]
                    //itemCatList[0]="afaaa";

                    for(var i=0;i<response.data.length;i++){
                        app.itemCatList[response.data[i].id]=response.data[i].name;
                    }

                    //重新手动渲染
                    app.$mount("#app");
                }
            )
        },
        shuaxing:function () {
            if (choose==1){
                this.findAllItemCategory();
                this.selectList({id:0});
            }if (choose==2){
                this.searchListApply(1);
            }


        }


    },

    //钩子函数 初始化了事件和
  /*  created: function () {
      
      //  this.searchList(1);
        this.findByParentId(0);

    }*/
  //修改钩子函数：默认查询一级分类列表。
    //钩子函数 初始化了事件和
    created: function () {
        //this.findByParentId(0);
        this.findAllItemCategory()
        this.selectList({id:0});
    }

})
