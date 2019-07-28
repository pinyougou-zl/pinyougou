var myChart = echarts.init(document.getElementById('chart1'));

// 异步加载数据
axios.get('/user/userActive.shtml').then(
    function (response) {//response.data = map
        // 填入数据
        myChart.setOption({
            title : {
                text: '活跃用户和非活跃用户统计',
                subtext: '数据库直查，绝对真实',
                x:'center'
            },
            tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: ['活跃用户','非活跃用户']
            },
            series : [
                {
                    name: '访问来源',
                    type: 'pie',
                    radius : '55%',
                    center: ['50%', '60%'],
                    data:[
                        {value:response.data.activeUser.length , name:'活跃用户'},
                        {value:response.data.inActiveUser.length, name:'非活跃用户'},
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

        })
    }
);