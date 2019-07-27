var app = new Vue({
    el:"#app",
    data:{
        username:'A'
    },
    methods:{
        //获取用户登录名
        loadUserName:function () {
            axios.post('/login/getname').then(function (response) {
                //获取数据
                app.username = response.data;
            });
        }
    },

    //钩子函数
    created:function () {
        this.loadUserName();
    }
})