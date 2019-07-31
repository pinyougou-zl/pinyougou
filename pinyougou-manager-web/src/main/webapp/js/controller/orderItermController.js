var app = new Vue({

    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        startTime: '0',
        endTime: '0',
        itemCatList: [],
        status: ['未审核', '已审核', '审核未通过', '已关闭'],
        searchEntity: {}
    },

    methods: {

        //获取商品所有分类信息的列表
        findAllItemCategory: function () {
            //获取商品的所有分类
            axios.post('/itemCat/findAll').then(function (response) {
                for (var i = 0; i < response.data.length; i++) {
                    //response.data[i].name;这个就是查询出来品牌对象里的名称
                    app.itemCatList[response.data[i].id] = response.data[i].name;
                }
                //重新手动渲染
                app.$mount("#app");
            }).catch(function (error) {
                console.log("123456")
            })
        },

        //分页查询
        searchList: function (curPage) {
            //如果我们需要条件查询，绑定该事件，会将参数带过去
            axios.post('/goods/search?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;
                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        searchOrderByCreateTime: function (value) {
            var that = this;
            var startTime = document.getElementById("startTime").value.trim();
            var endTime = document.getElementById("endTime").value.trim();

            app.startTime=startTime;
            app.endTime=endTime;
            axios.get('/orderItem/searchOrderByCreateTime', {
                params: {
                    pageNo: app.pageNo,
                    pages: app.pages,
                    startTime: startTime,
                    endTime: endTime,
                }
            }).then(function (response) {

                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;

                app.drawEchartPie(response.data);


            }).catch(function (error) {
                // alert(error.message)
            })

        },
        drawEchartPie() {
            // 基于准备好的dom，初始化echarts实例
            let myChart = echarts.init(document.getElementById('main'));

            var option = {
                title: {
                    text: app.startTime+'到'+app.endTime+'期间各商品销售饼状图',
                    subtext: '纯属虚构',
                    x: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']
                },
                series: [
                    {
                        name: '访问来源',
                        type: 'pie',
                        radius: '55%',
                        center: ['50%', '60%'],
                        data: [
                            {value: 335, name: '直接访问'},
                            {value: 310, name: '邮件营销'},
                            {value: 234, name: '联盟广告'},
                            {value: 135, name: '视频广告'},
                            {value: 1548, name: '搜索引擎'}
                        ],
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            };
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        }


    },
    //钩子函数 初始化了事件和
    created: function () {


    },
    mounted() {
        app.drawEchartPie();
    },
    update() {

    }

});

