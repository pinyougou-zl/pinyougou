var myChart = echarts.init(document.getElementById('chart1'));

// 异步加载数据
axios.get('/user/userCount.shtml').then(function (response) {
    // 填入数据
    myChart.setOption({
        title: {
            text: '最受用户喜欢的商品'
        },
        tooltip: {},
        legend: {
            data:['销售额']
        },
        xAxis: {
            data: response.data.goodsName
        },
        yAxis: {
            type:'value'
        },
        series: [{
            name: '销售额',
            type: 'bar',
            data: response.data.sellerNumber
        }]

    });
});