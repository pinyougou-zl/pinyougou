var app = new Vue({
    el:"#app",
    data:{
        list:[],
        pages:15,
        pageNo:1,
        entity:{},
        ids:[],
        searchEntity:{}
    },

    methods:{
        //更新状态的方法
        updateStatus:function (status) {
            axios.post('/seckillGoods/updateStatus?status='+status,this.ids).then(function (response) {
                if(response.data.success) {
                    app.searchList(1);
                }
            });
        },


        //查询列表的方法，并分页
        searchList:function (curPage) {
            axios.post('/seckillGoods/search?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;

                //总页数
                app.pages = response.data.pages;
            });
        }
    },

    //钩子函数,初始化函数
    created: function () {

        this.searchList(1);

    }
})