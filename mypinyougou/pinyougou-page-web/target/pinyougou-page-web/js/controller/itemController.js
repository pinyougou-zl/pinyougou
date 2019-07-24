var app = new Vue({
    el: "#app",
    data: {
        num:1,//商品的购买数量
        specificationItems:JSON.parse(JSON.stringify(skuList[0].spec)),//定义一个变量用于存储规格的数据
        sku:skuList[0]
    },
    methods: {
        addNum:function(num){
            num = parseInt(num);
            this.num+=num;//加或者减
            if(this.num<=1){
                this.num=1;
            }
        },
        selectSpecifcation:function(name,value){
            //设置值
            this.$set(this.specificationItems,name,value);
            this.search();
        },
        isSelected:function(name,value){
            if(this.specificationItems[name]==value){
                return true;
            }else{
                return false;
            }		
        },
        search:function(){
            for(var i=0;i<skuList.length;i++){
                var object = skuList[i];
                if(JSON.stringify(this.specificationItems)==JSON.stringify(skuList[i].spec)){
                    console.log(object);
                    this.sku=object;
                    break;
                }
            }
        },

        //添加方法
        //要想实现ajax跨域请求，要打开withCredentials属性，默认false
        addGoodsToCartList:function () {
            axios.get('http://localhost:9107/cart/addGoodsToCartList',{
                params:{
                    itemId:this.sku.id,
                    num:this.num
                },
                withCredentials:true
            }).then(function (response) {
                if(response.data.success) {
                    //添加购物车成功
                    window.location.href = "http://localhost:9107/cart.html";
                }else {
                    alert(response.data.message)
                }
            }).catch(function (error) {

            })
        },


    },

    //钩子函数 初始化了事件和
    created: function () {

    }

})