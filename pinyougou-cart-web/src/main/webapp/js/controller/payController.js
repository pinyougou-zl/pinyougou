var app = new Vue({
    el:"#app",
    data:{
        payObject:{},//封装支付的金额 二维码连接 交易订单号
        totalMoney:0
    },
    methods:{
        createNative:function () {
            var that=this;
            axios.get('/pay/createNative').then(
                function (response) {
                    //如果有数据
                    if(response.data){
                        app.payObject=response.data;
                        app.payObject.total_fee=app.payObject.total_fee/100;
                        //生成二维码
                        alert("生成二维码")
                        var qr = new QRious({
                            element:document.getElementById('qrious'),
                            size:250,
                            level:'H',
                            value:app.payObject.code_url
                        });
                        //已经生成二维码了
                        if(qr) {
                            //发送请求查询支付的状态
                            app.queryPayStatus(app.payObject.out_trade_no);
                        }
                    }
                }
            )
        },

        //查询支付状态的方法
        queryPayStatus:function (out_trade_no) {
            axios.get('/pay/queryPayStatus',{
                params:{
                    out_trade_no:out_trade_no
                }
            }).then(function (response) {
                if(response.data) {
                    if(response.data.success) {
                        //支付成功
                        alert("支付成功")
                        window.location.href="paysuccess.html?money="+app.payObject.total_fee;
                    }else {
                        //支付失败
                        alert("fail2222")
                        if(response.data.message == '支付超时') {
                            app.createNative();  //刷新二维码
                        }
                        window.location.href="payfail.html";
                    }
                }else {
                    alert("fail");
                }
            })
        }


    },
    //钩子函数
    created:function () {
        if(window.location.href.indexOf("pay.html") != -1) {
            //页面一加载就应当调用
            this.createNative();
        }else {
            let urlParamObject = this.getUrlParam();
            if(urlParamObject.money)
                this.totalMoney=urlParamObject.money;
        }
       /* //获取携带过来的参数
        var request = this.getUrlParam();
        //获取参数值
        app.totalMoney = request.money;*/
    }

})