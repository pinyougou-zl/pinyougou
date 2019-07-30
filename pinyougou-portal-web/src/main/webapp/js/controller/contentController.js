var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        contentList:[],
        list3:[{"id":1,"itemCatList":[{"id":2,"name":"电子书刊","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":7,"name":"音像","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":11,"name":"英文原版","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":18,"name":"文艺","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":24,"name":"少儿","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":30,"name":"人文社科","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":40,"name":"经管励志","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":45,"name":"生活","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":51,"name":"科技","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":59,"name":"教育","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":65,"name":"港台图书","parentId":1,"sellerId":"qiandu","status":"1","typeId":35},{"id":70,"name":"其它","parentId":1,"sellerId":"qiandu","status":"1","typeId":35}],"name":"图书、音像、电子书刊","parentId":0,"status":null,"typeId":null},{"id":74,"itemCatList":[{"id":75,"name":"大家电","parentId":74,"sellerId":"qiandu","status":"1","typeId":35},{"id":89,"name":"生活电器","parentId":74,"sellerId":"qiandu","status":"1","typeId":35},{"id":108,"name":"厨房电器","parentId":74,"sellerId":"qiandu","status":"1","typeId":35},{"id":126,"name":"个护健康","parentId":74,"sellerId":"qiandu","status":"1","typeId":35},{"id":142,"name":"五金家装","parentId":74,"sellerId":"qiandu","status":"1","typeId":35},{"id":1192,"name":"小家电","parentId":74,"sellerId":"qiandu","status":"1","typeId":35}],"name":"家用电器","parentId":0,"status":null,"typeId":null},{"id":161,"itemCatList":[{"id":162,"name":"电脑整机","parentId":161,"sellerId":"qiandu","status":"1","typeId":35},{"id":171,"name":"电脑配件","parentId":161,"sellerId":"qiandu","status":"1","typeId":35},{"id":186,"name":"外设产品","parentId":161,"sellerId":"qiandu","status":"1","typeId":35},{"id":202,"name":"网络产品","parentId":161,"sellerId":"qiandu","status":"1","typeId":35},{"id":210,"name":"办公设备","parentId":161,"sellerId":"qiandu","status":"1","typeId":35},{"id":229,"name":"文具/耗材","parentId":161,"sellerId":"qiandu","status":"1","typeId":35},{"id":244,"name":"服务产品","parentId":161,"sellerId":"qiandu","status":"1","typeId":35}],"name":"电脑、办公","parentId":0,"status":null,"typeId":null},{"id":249,"itemCatList":[{"id":250,"name":"面部护肤","parentId":249,"sellerId":"qiandu","status":"1","typeId":35},{"id":256,"name":"身体护肤","parentId":249,"sellerId":"qiandu","status":"1","typeId":35},{"id":264,"name":"口腔护理","parentId":249,"sellerId":"qiandu","status":"1","typeId":35},{"id":269,"name":"女性护理","parentId":249,"sellerId":"qiandu","status":"1","typeId":35},{"id":274,"name":"洗发护发","parentId":249,"sellerId":"qiandu","status":"1","typeId":35},{"id":281,"name":"香水彩妆","parentId":249,"sellerId":"qiandu","status":"1","typeId":35}],"name":"个护化妆","parentId":0,"status":null,"typeId":null},{"id":290,"itemCatList":[{"id":291,"name":"钟表","parentId":290,"sellerId":"qiandu","status":"1","typeId":35}],"name":"钟表","parentId":0,"status":null,"typeId":null},{"id":296,"itemCatList":[{"id":297,"name":"奶粉","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":300,"name":"营养辅食","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":309,"name":"尿裤湿巾","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":314,"name":"喂养用品","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":322,"name":"洗护用品","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":331,"name":"童车童床","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":341,"name":"寝居服饰","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":348,"name":"妈妈专区","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":358,"name":"童装童鞋","parentId":296,"sellerId":"qiandu","status":"1","typeId":35},{"id":374,"name":"安全座椅","parentId":296,"sellerId":"qiandu","status":"1","typeId":35}],"name":"母婴","parentId":0,"status":null,"typeId":null},{"id":378,"itemCatList":[{"id":379,"name":"进口食品","parentId":378,"sellerId":"qiandu","status":"1","typeId":35},{"id":386,"name":"地方特产","parentId":378,"sellerId":"qiandu","status":"1","typeId":35},{"id":397,"name":"休闲食品","parentId":378,"sellerId":"qiandu","status":"1","typeId":35},{"id":405,"name":"粮油调味","parentId":378,"sellerId":"qiandu","status":"1","typeId":35},{"id":412,"name":"饮料冲调","parentId":378,"sellerId":"qiandu","status":"1","typeId":35},{"id":420,"name":"食品礼券","parentId":378,"sellerId":"qiandu","status":"1","typeId":35},{"id":425,"name":"茗茶","parentId":378,"sellerId":"qiandu","status":"1","typeId":35}],"name":"食品饮料、保健食品","parentId":0,"status":null,"typeId":null},{"id":438,"itemCatList":[{"id":439,"name":"维修保养","parentId":438,"sellerId":"qiandu","status":"1","typeId":35},{"id":456,"name":"车载电器","parentId":438,"sellerId":"qiandu","status":"1","typeId":35},{"id":467,"name":"美容清洗","parentId":438,"sellerId":"qiandu","status":"1","typeId":35},{"id":474,"name":"汽车装饰","parentId":438,"sellerId":"qiandu","status":"1","typeId":35},{"id":486,"name":"安全自驾","parentId":438,"sellerId":"qiandu","status":"1","typeId":35}],"name":"汽车用品","parentId":0,"status":null,"typeId":null},{"id":495,"itemCatList":[{"id":496,"name":"适用年龄","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":503,"name":"遥控/电动","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":509,"name":"毛绒布艺","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":512,"name":"娃娃玩具","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":516,"name":"模型玩具","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":520,"name":"健身玩具","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":525,"name":"动漫玩具","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":529,"name":"益智玩具","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":534,"name":"积木拼插","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":539,"name":"DIY玩具","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":543,"name":"创意减压","parentId":495,"sellerId":"qiandu","status":"1","typeId":35},{"id":546,"name":"乐器相关","parentId":495,"sellerId":"qiandu","status":"1","typeId":35}],"name":"玩具乐器","parentId":0,"status":null,"typeId":null},{"id":558,"itemCatList":[{"id":559,"name":"手机通讯","parentId":558,"sellerId":"qiandu","status":"1","typeId":35},{"id":562,"name":"运营商","parentId":558,"sellerId":"qiandu","status":"1","typeId":35},{"id":567,"name":"手机配件","parentId":558,"sellerId":"qiandu","status":"1","typeId":35}],"name":"手机","parentId":0,"status":null,"typeId":null},{"id":580,"itemCatList":[{"id":581,"name":"摄影摄像","parentId":580,"sellerId":"qiandu","status":"1","typeId":35},{"id":591,"name":"数码配件","parentId":580,"sellerId":"qiandu","status":"1","typeId":35},{"id":604,"name":"智能设备","parentId":580,"sellerId":"qiandu","status":"1","typeId":35},{"id":614,"name":"时尚影音","parentId":580,"sellerId":"qiandu","status":"1","typeId":35},{"id":625,"name":"电子教育","parentId":580,"sellerId":"qiandu","status":"1","typeId":35}],"name":"数码","parentId":0,"status":null,"typeId":null},{"id":633,"itemCatList":[{"id":634,"name":"家纺","parentId":633,"sellerId":"qiandu","status":"1","typeId":35},{"id":648,"name":"灯具","parentId":633,"sellerId":"qiandu","status":"1","typeId":35},{"id":660,"name":"生活日用","parentId":633,"sellerId":"qiandu","status":"1","typeId":35},{"id":667,"name":"家装软饰","parentId":633,"sellerId":"qiandu","status":"1","typeId":35},{"id":683,"name":"清洁用品","parentId":633,"sellerId":"qiandu","status":"1","typeId":35},{"id":691,"name":"宠物生活","parentId":633,"sellerId":"qiandu","status":"1","typeId":35}],"name":"家居家装","parentId":0,"status":null,"typeId":null},{"id":699,"itemCatList":[{"id":700,"name":"烹饪锅具","parentId":699,"sellerId":"qiandu","status":"1","typeId":35},{"id":711,"name":"刀剪菜板","parentId":699,"sellerId":"qiandu","status":"1","typeId":35},{"id":718,"name":"厨房配件","parentId":699,"sellerId":"qiandu","status":"1","typeId":35},{"id":724,"name":"水具酒具","parentId":699,"sellerId":"qiandu","status":"1","typeId":35},{"id":733,"name":"餐具","parentId":699,"sellerId":"qiandu","status":"1","typeId":35},{"id":739,"name":"茶具/咖啡具","parentId":699,"sellerId":"qiandu","status":"1","typeId":35}],"name":"厨具","parentId":0,"status":null,"typeId":null},{"id":749,"itemCatList":[{"id":750,"name":"女装","parentId":749,"sellerId":"qiandu","status":"1","typeId":35},{"id":784,"name":"男装","parentId":749,"sellerId":"qiandu","status":"1","typeId":35},{"id":813,"name":"内衣","parentId":749,"sellerId":"qiandu","status":"1","typeId":35},{"id":836,"name":"服饰配件","parentId":749,"sellerId":"qiandu","status":"1","typeId":35}],"name":"服饰内衣","parentId":0,"status":null,"typeId":null},{"id":865,"itemCatList":[{"id":866,"name":"流行男鞋","parentId":865,"sellerId":"qiandu","status":"1","typeId":35},{"id":881,"name":"时尚女鞋","parentId":865,"sellerId":"qiandu","status":"1","typeId":35}],"name":"鞋靴","parentId":0,"status":null,"typeId":null}],
        item:"item",
        ids:[],
        keywords:'',  //获取关键字
        searchEntity:{},
        flag:false,
        itemCat02:[],
        itemCat03:[]

    },
    methods: {
        searchList:function (curPage) {
            axios.post('/content/search?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/content/findAll').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/content/findPage',{params:{
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
            axios.post('/content/add',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/content/update',this.entity).then(function (response) {
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
            axios.get('/content/findOne/'+id).then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/content/delete',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //根据广告类型id查询列表
        findByCategoryId:function (categoryId) {
            axios.get('/content/findByCategoryId/'+categoryId).then(function(response) {
                app.contentList = response.data;
            }).catch(function (error) {
                console.log("123456");
            })
        },

        //添加搜索方法
        doSearch:function () {
            window.location.href="http://localhost:9104/search.html?keywords="+encodeURIComponent(this.keywords);
        },
        goodsItem:function (parentId) {
            axios.post('/itemCat/goodsItem/'+parentId).then(
                function (response) {
                    app.list=response.data

                    /*app.list=JSON.parse(response.data)*/
                }
            )
        },

        /*selectStyle:function(){

            this.flag=true;
        },*/
        enter:function(){
            /*document.getElementsByClassName("item")[e].className="item hover"
            document.getElementsByClassName("item-list")[0].style="display:block"
            document.getElementsByTagName("body")[0].className="bg";
            e.preventDefault()
            e.target.className ='hover';*/
            this.flag=true

        },
        leave:function(){
           /* document.getElementsByClassName("item")[e].className = "item"
            document.getElementsByClassName("item-list")[0].style = "display:none"
            e.target.className = '';
            e.target.className = '';*/
           this.flag=false

        }



    },
    //钩子函数 初始化了事件和
    created: function () {
        this.findByCategoryId(1);
        this.goodsItem(0)
    }

})
