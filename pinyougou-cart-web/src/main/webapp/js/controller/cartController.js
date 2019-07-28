var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        totalMoney:0,//总金额
        totalNum:0,//总数量
        cartList: [],
        entity: {},
        ids: [],
        searchEntity: {},
        addressList:[],  //地址列表
        address:{},   //地址信息
        order:{paymentType:'1'},  //订单信息
    },
    methods:{
        //查询所有的购物车的列表数据
        findCartList:function () {
            axios.get('/cart/findCartList').then(
                function (response) {
                    app.cartList=response.data;//List<Cart>   cart { List<ORDERiMTE> }
                    app.totalMoney=0;
                    app.totalNum=0;
                    for(var i=0;i<response.data.length;i++){
                        var obj = response.data[i];//Cart
                        for(var n=0;n<obj.orderItemList.length;n++){
                            var objx = obj.orderItemList[n];//ORDERiMTE
                            app.totalMoney+=objx.totalFee;
                            app.totalNum+=objx.num;
                        }
                    }

                }
            )
        },
        //向已有的购物车中添加商品
        addGoodsToCartList:function (itemId,num) {
            axios.get('/cart/addGoodsToCartList?itemId='+itemId+'&num='+num).then(
                function (response) {
                    if(response.data.success){
                        app.findCartList();
                    }
                }
            )
        },

        //查询地址列表的信息
        findAddressList:function () {
            axios.get('/address/findAddressListByUserId').then(function (response) {
                app.addressList = response.data;
                for(var i=0;i<app.addressList.length;i++) {
                    if (app.addressList[i].isDefault == '1') {
                        app.address = app.addressList[i];
                        break;
                    }
                }
            });
        },

        //新增方法
        selectAddress:function (address) {
            this.address=address;
        },
        isSelectedAddress:function (address) {
            if(address==this.address){
                return true;
            }
            return false;
        },

        selectType:function (type) {
            console.log(type);
            this.$set(this.order,'paymentType',type);
        },

        //添加一个方法
        submitOrder:function () {
            //设置值
            this.$set(this.order,'receiverAreaName',this.address.address);
            this.$set(this.order,'receiverMobile',this.address.mobile);
            this.$set(this.order,'receiver',this.address.contact);
            axios.post('/order/add', this.order).then(
                function (response) {
                    if(response.data.success){
                        //跳转到支付页面
                        window.location.href="pay.html";
                    }else{
                        alert(response.data.message);
                    }
                }
            )
        }



    },
    created:function () {
        this.findCartList();
        //需要进行判断
        if(window.location.href.indexOf("getOrderInfo.html")!=-1) {
            this.findAddressList()
        }
    }
});