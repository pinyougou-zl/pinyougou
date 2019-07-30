var app = new Vue({
    el:'#app',
    data:{
        pages:15,
        pageNo:1,
        list:[],
        entity:{specification:{},optionList:[]},
        ids:[],
        searchEntity:{}
    },
    methods:{
        //查询所有
        findAll:function() {
            axios.get('/specification/findAll').then(function (response) {
                app.list = response.data;
            }).catch(function (error) {

            })
        },

        //分页查询
        searchList:function(curPage) {
            axios.post('/specification/search?PageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;
                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            })
        },


        //添加行
        addTableRow:function () {
            this.entity.optionList.push({});
        },
        //删除行 splice的参数是，根据index传的值删除一个
        removeTableRow:function (index) {
            this.entity.optionList.splice(index,1);
        },

        //保存和修改方法
        save:function () {
            if(this.entity.specification.id!=null) {
                this.update();
            }else {
                this.add();
            }
        },
        //真正的保存方法
        add:function () {
            axios.post('/specification/add',this.entity).then(function (response) {
                if(response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("保存失败")
            })
        },

        //回显，一点修改就会显示出品牌信息
        findOne:function (id) {
            axios.post('/specification/findOne/'+id).then(function (response) {
                app.entity = response.data;
            })
        },

        //进行修改
        update:function () {
            axios.post('/specification/update',this.entity).then(function (response) {
                if(response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {

            })
        },

        //进行删除
        dele:function () {
            axios.post('/specification/delete',this.ids).then(function (response) {
                if(response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
            })
        }
    },
    //钩子函数 初始化了事件
    created: function () {
        this.searchList(1);
    }
})