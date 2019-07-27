var app = new Vue({
    el: "#app",
    data: {
        searchMap:{'keywords':'','category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sortType':''},//作为条件查询的对象绑定搜索条件的对象
        pageLabels:[],//页码存储的变量
        resultMap:{brandList:[]},//返回的结果对象
        searchEntity: {},
        //页码...的显示
        preDott:false,
        nextDott:false
    },
    methods: {
        //根据搜索的条件 执行查询 返回结果 resultmap 点击的时候调用
        search:function () {
            axios.post('/itemSearch/search.',this.searchMap).then(
                function (response) {//response.data=map 会有集合数据
                    app.resultMap=response.data;
                    app.buildPageLabel();
                }
            )
        },
        //添加搜索方法
        addSearchItem:function(key,value) {
            if(key=='category' || key=='brand' || key=='price') {
                this.searchMap[key] = value;
            }else {
                this.searchMap.spec[key] = value;
            }
            this.search();
        },

        //撤销搜索项，也就是面包屑,传入key
        removeSearch:function(key) {
            if(key=='category' || key=='brand' || key=='price') {
                this.searchMap[key] = '';  //清空
            }else {
                delete  this.searchMap.spec[key];
            }
            this.search();
        },

        //页码显示的方法,构建分页标签的数据
        buildPageLabel:function () {
            this.pageLabels = [];  //初始化为空
            //显示已当前页为中心的5个页码
            let firstPage = 1;
            let lastPage = this.resultMap.totalPages; //总页数
            if (this.resultMap.totalPages > 5) {
                //判断 如果当前的页码 小于等于3  pageNo<=3      1 2 3 4 5  显示前5页
                if (this.searchMap.pageNo <= 3) {
                    firstPage = 1;
                    lastPage = 5;
                    this.preDott=false;
                    this.nextDott=true;
                } else if (this.searchMap.pageNo >= (this.resultMap.totalPages - 2)) {//如果当前的页码大于= 总页数-2    98 99 100
                    firstPage = this.resultMap.totalPages - 4;
                    lastPage = this.resultMap.totalPages;
                    this.preDott=true;
                    this.nextDott=false;
                } else {
                    firstPage = this.searchMap.pageNo - 2;
                    lastPage = this.searchMap.pageNo + 2;
                    this.preDott=true;
                    this.nextDott=true;
                }
            } else {
                this.preDott=false;
                this.nextDott=false;
            }
            for (let i = firstPage; i <= lastPage; i++) {
                this.pageLabels.push(i);
            }
        },

        //点击页码查询
        queryByPage:function (pageNo) {
            var pageNo = parseInt(pageNo);
            if(pageNo >= this.resultMap.totalPages) {
                pageNo = this.resultMap.totalPages;
            }

            if(pageNo < 1) {
                pageNo = 1;
            }
            this.searchMap.pageNo = pageNo;
            this.search();
        },

        //添加页码搜索的清空方法
        clear:function () {
            //也就是还原默认
            this.searchMap={'keywords':this.searchMap.keywords,'category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sortType':''};
        },

        //添加方法，排序
        doSort:function(sortField,sortType) {
            this.searchMap.sortField = sortField;
            this.searchMap.sortType = sortType;
            this.search();
        },

        //如果用户输入的是品牌，那就不能显示品牌列表了
        isKeywordsIsBrand:function () {
            //遍历品牌列表
            for(var i =0;i<this.resultMap.brandList.length;i++) {
                if(this.searchMap.keywords.indexOf(this.resultMap.brandList[i].text)!=-1){
                    this.searchMap.brand = this.resultMap.brandList[i].text;
                    return true
                }
            }
            return false;
        }
    },


    created: function () {
        var urlParam = this.getUrlParam();
        if(urlParam.keywords !=undefined && urlParam.keywords != null) {
            //进行解码，获取keywords
            this.searchMap.keywords = decodeURIComponent(urlParam.keywords);
            this.search();
        }
    }
});