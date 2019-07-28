var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        brandOptions:[],//绑定品牌列表下拉框数据的
        specOptions:[{id:1,text:"机身内存"}],//绑定规格的数据列表，
        entity:{customAttributeItems:[]},  //初始化数据的
        ids:[],
        searchEntity:{},
        itemCatList:[],
        status:['未审核','已审核','审核未通过','已关闭']

    },
    methods:{
        searchList:function (curPage) {
            axios.post('/itemCat/findPage?pageNo='+curPage).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //获取商品所有分类信息的列表
        findAllItemCategory:function() {
            //获取商品的所有分类
            axios.post('/itemCat/findAll').then(function (response) {
                for(var i=0;i<response.data.length;i++) {
                    //response.data[i].name;这个就是查询出来品牌对象里的名称
                    app.itemCatList[response.data[i].id] = response.data[i].name;
                }
                //重新手动渲染
                app.$mount("#app");
            }).catch(function (error) {
                console.log("123456")
            })
        }
        /*findParentName:function (parentId) {
            var str="";
            //$.ajaxSettings.async = false;
            $.ajaxSetup({

                async:false


            });
            axios.post('/itemCat/findParentName/'+parentId).then(
                function (response) {
                    if (response.data.success) {
                        str=response.data.message;
                         alert(str)
                    }

                }

            );
            return str
        }*/
        /*async funA(parentId){
            var str="";
            var res =  await axios.post('/itemCat/findParentName/'+parentId).then(
                function (response) {
                    if (response.data.success) {
                        str=response.data.message;
                      alert(str)

                    }

                }

            );
            return str//这里的res就是你axios请求回来的结果了
        }*/
        ,
        updateStatus:function (status) {
            axios.post('/itemCat/updateStatus/'+status,this.ids).then(
                function (response) {
                    if (response.data.success) {
                        app.searchList(1);
                    }else {
                        alert(response.data.message)
                    }
                }
            )
        }
        ,

        //添加行
        addTableRow:function () {
            this.entity.customAttributeItems.push({});
        },
        //删除行 splice的参数是，根据index传的值删除一个
        removeTableRow:function (index) {
            this.entity.customAttributeItems.splice(index,1);
        },

        findBrandIds:function() {
            axios.get('/brand/findAll').then(function (response) {
                let brandList = response.data;//[{id,name}]
                for(var i=0;i<brandList.length;i++){
                    app.brandOptions.push({id:brandList[i].id,text:brandList[i].name});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        findSpecList:function () {
            axios.get('/specification/findAll').then(function (response) {
                let specList = response.data;
                for(var i=0;i<specList.length;i++) {
                    app.specOptions.push({id:specList[i].id,text:specList[i].specName});
                }
            }).catch(function (error) {
            })
        },

        //修改之前，先进行回显
        findOne:function (id) {
            //使用restful风格
            axios.get('/typeTemplate/findOne/'+id).then(function (response) {
                app.entity = response.data;
                app.entity.brandIds=JSON.parse(app.entity.brandIds);
                app.entity.customAttributeItems=JSON.parse(app.entity.customAttributeItems);
                app.entity.specIds=JSON.parse(app.entity.specIds);
            }).catch(function (error) {

            })
        },

        //进行修改
        update:function () {
            axios.post('/typeTemplate/update',this.entity).then(function (response) {
                if(response.data.success) {
                    //修改成功刷新页面
                    app.searchList(1)
                }
            }).catch(function (error) {
                //这就不打了
            })
        },

        //进行新增
        add:function () {
            axios.post('/typeTemplate/add',this.entity).then(function (response) {
                if(response.data.success) {
                    //修改成功刷新页面
                    app.searchList(1)
                }
            }).catch(function (error) {
                //这就不打了
            })
        },

        //删除方法
        dele:function() {
            axios.post('/typeTemplate/delete',this.ids)  .then(function (response) {
                if(response.data.success) {
                    //修改成功刷新页面
                    app.searchList(1)
                }
            }).catch(function (error) {
                //这就不打了
            })
        },

        //增加跟修改需要进行判断
        save:function () {
            if(this.entity.id!=null) {
                //有id进行修改
                app.update();
            }else {
                app.add();
            }
        },

        //优化json
        jsonToString:function (list, key) {
            //用于循环遍历，获取对象中的属性的值，拼接字符串
            var list = JSON.parse(list);
            var str = "";
            for(var i= 0;i<list.length;i++) {
                var obj = list[i];
                str += obj[key]+",";
            }
            if(str.length>0) {
                //截取字符串，从0索引开始，截取到倒数第二位
                str = str.substring(0,str.length-1);
            }
            return str;
        }

    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);
        this.findAllItemCategory();
        /*this.findBrandIds();
        this.findSpecList();*/

    }
})