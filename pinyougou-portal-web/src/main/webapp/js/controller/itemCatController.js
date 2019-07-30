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
        goodsItem:function (parentId) {
            axios.post('/itemCat/goodsItem/'+parentId).then(
                function (response) {
                    app.list=response.data
                    /*app.list=JSON.parse(response.data)*/
                }
            )
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
        this.goodsItem(0)

    }

})
