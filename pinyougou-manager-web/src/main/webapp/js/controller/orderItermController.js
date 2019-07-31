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
        pieStartTime:'0',
        pieEndTime:'0',
        searchEntity: {},


        options: {
            title: {text: '', subtext: '', x: 'center'},
            tooltip: {trigger: 'item', formatter: "{a} <br/>{b} : {c} ({d}%)"},
            legend: {orient: 'vertical', left: 'left', data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']},
            series: [
                {
                    name: '',
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '60%'],
                    data: [

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
        },

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
        searchOrderByCreateTime: function () {

            var that = this;
            var startTime = document.getElementById("startTime").value.trim();
            var endTime = document.getElementById("endTime").value.trim();

            //进行开始时间转换
            var date = new Date(parseInt(startTime + "000"));
            Y = date.getFullYear() + '-';
            M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
            D = date.getDate() + ' ';
            h = date.getHours() + ':';
            m = date.getMinutes() + ':';
            s = date.getSeconds();
            var titleStartTime = Y + M + D + h + m + s;
            app.pieStartTime=titleStartTime;

            //进行借结束转换
            var date = new Date(parseInt(endTime + "000"));
            Y1 = date.getFullYear() + '-';
            M1 = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
            D1 = date.getDate() + ' ';
            h1 = date.getHours() + ':';
            m1 = date.getMinutes() + ':';
            s1 = date.getSeconds();
            var titleEndTime = Y1 + M1 + D1 + h1 + m1 + s1;
            app.pieEndTime=titleEndTime
            // console.log(Y+M+D+h+m+s);

            axios.get('/orderItem/searchOrderByCreateTime', {
                params: {
                    pageNo: that.pageNo,
                    pages: that.pages,
                    startTime: startTime,
                    endTime: endTime,
                }
            }).then(function (response) {

                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;
                //总页数
                var data1 = response.data;
                for(var i=0;i<data1.length;i++){


                }

                app.options = {
                    title: {text: app.pieStartTime+'到'+app.pieEndTime+' 商品销售饼状图', subtext: '', x: 'center'},
                    tooltip: {trigger: 'item', formatter: "{a} <br/>{b} : {c} ({d}%)"},
                    legend: {orient: 'vertical', left: 'left', data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']},
                    series: [
                        {
                            name: '访问来源',
                            type: 'pie',
                            radius: '55%',
                            center: ['50%', '60%'],
                            data: [
                                {value: 1999, name: '测试手机 移动3G 16G'},
                                {value: 1499, name: 'DOTA2 移动4G 32G'},
                                {value: 1499, name: 'DOTA2 移动4G 32G'},
                                {value: 995, name: '锤子手机不好 移动3G 16G'},
                                {value: 711.0999999999999	, name: '高端皮具护理 联通3G 6寸'},
                                {value: 500	, name: '黑马26期造的手机 移动3G 16G'},
                                {value: 7.099999999999998, name: '古董 移动3G 6寸'},
                                {value: 2, name: '我的测试手机 移动3G 32G\t'},
                                {value: 2, name: '我的测试手机 移动3G 32G'},
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
                that.drawEchartPie();

            }).catch(function (error) {
                // alert(error.message)
            })

        },
        drawEchartPie: function () {
            var vm = this;

            // 基于准备好的dom，初始化echarts实例
            let myChart = echarts.init(document.getElementById('main'));


            //防止越界，重绘canvas
            window.onresize = myChart.resize;
            myChart.setOption(vm.options);
        },


    },
    //钩子函数 初始化了事件和
    created: function () {


    },
    mounted() {
        this.drawEchartPie();

    },
    update() {
        if (!this.myChart) {
            this.setEchart();
        }
        this.chartChange();
    },
    computed: {
        origin() {
            return this.data;
        },
        opt() {
            let that = this;
            let obj = {
                color: ['#606c94'],
                tooltip: {},
                toolbox: {
                    show: true,
                    feature: {
                        saveAsImage: {show: true}
                    }
                },
                label: {
                    normal: {
                        show: true,
                        position: 'inside',
                        formatter: '{c}'
                    },
                    emphasis: {
                        show: true
                    }
                },
                xAxis: {
                    type: 'value',
                },
                yAxis: {
                    data: that.origin[that.type][that.pagePick].key,
                    axisLabel: {
                        interval: 0,
                        rotate: -30
                    }
                },
                series: [{
                    name: that.origin.title,
                    type: 'bar',
                    data: that.origin[that.type][that.pagePick].val,
                    barMaxWidth: '30',
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                }]
            }
            return obj;
        },
        type() {
            if (this.typePick == '数值') {
                return 'numeric';
            } else if (this.typePick == '百分比') {
                return 'percent';
            } else {
                return 'numeric';
            }
        }
    },


});

