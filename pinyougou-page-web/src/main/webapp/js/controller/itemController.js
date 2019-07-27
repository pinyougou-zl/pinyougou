var app = new Vue({
    el: "#app",
    data: {
        num:1,//��Ʒ�Ĺ�������
        specificationItems:JSON.parse(JSON.stringify(skuList[0].spec)),//����һ���������ڴ洢��������
        sku:skuList[0]
    },
    methods: {
        addNum:function(num){
            num = parseInt(num);
            this.num+=num;//�ӻ��߼�
            if(this.num<=1){
                this.num=1;
            }
        },
        selectSpecifcation:function(name,value){
            //����ֵ
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

        //��ӷ���
        //Ҫ��ʵ��ajax��������Ҫ��withCredentials���ԣ�Ĭ��false
        addGoodsToCartList:function () {
            axios.get('http://localhost:9107/cart/addGoodsToCartList',{
                params:{
                    itemId:this.sku.id,
                    num:this.num
                },
                withCredentials:true
            }).then(function (response) {
                if(response.data.success) {
                    //��ӹ��ﳵ�ɹ�
                    window.location.href = "http://localhost:9107/cart.html";
                }else {
                    alert(response.data.message)
                }
            }).catch(function (error) {

            })
        },


    },

    //���Ӻ��� ��ʼ�����¼���
    created: function () {

    }

})