var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        name:'',//用户名
        smsCode:'',//验证码的值
        ids:[],
        searchEntity:{}
    },
    methods: {
        //注册
        register:function () {
            axios.post('/user/add/'+this.smsCode,this.entity).then(function (response) {
                if(response.data.success){
                    //跳转到登录页面
                    window.location.href="home-index.html";
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //点击a标签的时候 调用方法 发送请求 发送验证码
        createSmsCode:function () {
            axios.get('/user/sendCode?phone='+this.entity.phone).then(
                function (response) {//result
                    alert(response.data.message);
                }
            )
        },
        getName:function () {
            axios.get('/login/name').then(
                function (response) {
                    app.name=response.data;
                }
            )
        }
    },
    created: function () {
      this.getName();
    }

})
