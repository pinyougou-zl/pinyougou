var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        itemCat1List:[],  //一级分类列表
        itemCat2List:[],  //二级分类列表
        itemCat3List:[],  //三级分类列表
        brandTextList:[],
        specList:[],  //品牌的规格选项列表
        image_entity:{color:'',url:''},
        entity:{tbGoods:{},goodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},itemList:[]},  //组合对象,在描述中增加图片属性
        ids:[],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/goods/search?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/goods/findAll').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/goods/findPage',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //增加修改方法
        update:function() {
          //获取内容那个发送请求
            //获取富文本编辑器中的内容传递给对象
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/update',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.entity = {goods:{},goodsDesc:{},itemList:[]};
                    //存储完之后都进行清空
                    editor.html("");  //清空
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //该方法只要不在生命周期的
        add:function () {
            //获取富文本编辑器中的内容传递给对象
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/add',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.entity = {goods:{},goodsDesc:{},itemList:[]};
                    //存储完之后都进行清空
                    editor.html("");  //清空
                    window.location.href='goods.html';
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/goods/update',this.entity).then(function (response) {
                alert("进行修改")
                console.log(response);
                if(response.data.success){
                    //app.searchList(1);
                    //修改成功跳回页面
                    window.location.href='goods.html';

                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.tbGoods.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/goods/findOne/'+id).then(function (response) {
                app.entity=response.data;
                //赋值到文本编辑器
                editor.html(app.entity.goodsDesc.introduction);
                //转换JSON显示
                app.entity.goodsDesc.itemImages = JSON.parse(app.entity.goodsDesc.itemImages)
                //读取文件的数据
                app.entity.goodsDesc.customAttributeItems =
                    JSON.parse(app.entity.goodsDesc.customAttributeItems);
                //展示规格的数据
                app.entity.goodsDesc.specificationItems=JSON.parse(app.entity.goodsDesc.specificationItems);
                //获取SKU列表
                for(var i=0;i<app.entity.itemList.length;i++) {
                    var obj = app.entity.itemList[i];  //{spec:{}}
                    //取出俩的是json字符串，需要转换
                    obj.spec=JSON.parse(obj.spec);
                }

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //添加一个方法用于判断
        isChecked:function(specName,specValue) {
            var obj = this.searchObjectByKey(this.entity.goodsDesc.specificationItems,specName,'attributeName');
            if(obj != null) {
                if(obj.attributeValue.indexOf(specValue) != -1) {
                    return true;
                }
             }
            return false;
        },

        dele:function () {
            axios.post('/goods/delete',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //文件上传的方法
        //1、模拟表单，设置数据
        //2、发送ajax请求，上传图片
        upload:function () {
            //模拟创建一个表单对象
            var formData = new FormData();
            //参数formData.append('file' 中的file 为表单的参数名  必须和 后台的file一致
            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            formData.append("file",file.files[0])
            axios({
                url: 'http://localhost:9110/upload/uploadFile',
                //data就是表单数据
                data: formData,
                method: 'post',
                //指定头信息：
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials:true
            }).then(function (response) {
                if (response.data.success) {
                    //app.imageurl=response.data.message;//url地址
                    app.image_entity.url = response.data.message;
                } else {
                    alert(response.data.message);
                }
            })
        },
        //添加图片
        addImageEntity:function () {
            this.entity.goodsDesc.itemImages.push(this.image_entity);
        },

        //移除图片
        remove_image_entity:function (index) {
            //用于删除数组中的元素，第一个参数为 要删除的元素的索引  第二个参数为要删除的个数。
            this.entity.goodsDesc.itemImages.splice(index,1);
        },

        //添加方法
        findItemCatList:function () {
            axios.get('/itemCat/findByParentId/0').then(function (response) {
                app.itemCat1List = response.data;
            }).catch(function (error) {
                console.log("123456");
            })
        },

        //增加方法，当点击复选框的时候并影响变量
        //当点击 复选框的时候 调用 影响变量 entity.goodsDesc.specificationItems的值
        //entity.goodsDesc.specificationItems:[{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}]
        updateChecked:function ($event, specName, specValue) {
            //如果有对象
            var searchObject = this.searchObjectByKey(this.entity.goodsDesc.specificationItems,specName,'attributeName');
            if(searchObject != null) {
                //判断是否被勾选，如果勾选。添加数据
                if($event.target.checked) {
                    //规格选的值，添加到对象中的attributeValue中
                    searchObject.attributeValue.push(specValue);
                } else {
                    //没有被勾选就移除
                    searchObject.attributeValue.splice(searchObject.attributeValue.indexOf(specValue),1);
                    //判断如果数组的长度为0，也是要移除对象的
                    if(searchObject.attributeValue.length==0) {
                        this.entity.goodsDesc.specificationItems.splice(this.entity.goodsDesc.specificationItems.indexOf(searchObject),1);
                    }
                }
            }else {
                //如果没有对象，直接添加对象即可
                this.entity.goodsDesc.specificationItems.push({
                    'attributeName':specName,
                    'attributeValue':[specValue]
                });
            }
        },

        /**
         *
         * @param list 从该数组中查询[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}]
         * @param specName  指定查询的属性的具体值 比如 网络
         * @param key  指定从哪一个属性名查找  比如：attributeName
         * @returns {*}
         * 根据attributeName从变量中找对象
         * key表示要找的属性值 attributeName
         */
        searchObjectByKey:function (list,specName,key) {
            //遍历数组
            for(var i = 0;i<list.length;i++) {
                var obj = list[i];  //代表一个对象
                if(specName==obj[key]) {
                    return obj;
                }
            }
            //走到这，也就是指定的属性跟list中的不一样，返回null
            return null;
        },

        //点击复选框的时候，调用生成sku列表的变量
        createList:function () {
            //1、定义初始化的值
            this.entity.itemList=[{'price':0,'num':0,'status':'0','isDefault':'0',spec:{}}];
            //2、循环遍历entity.goodsDesc.specificationItems;
            var specificationItemsObject = this.entity.goodsDesc.specificationItems;
            for(var i=0;i<specificationItemsObject.length;i++) {
                var obj = specificationItemsObject[i];
                //拼接要的格式
                this.entity.itemList = this.addColumn(
                    this.entity.itemList,
                    obj.attributeName,
                    obj.attributeValue
                );
            }

        },

        //集合拼接的方法
        addColumn:function (list,columnName,columnValue) {
            var newList=[];
            for (var i=0;i<list.length;i++) {
                var oldRow = list[i];
                for(var j=0;j<columnValue.length;j++) {
                    //进行序列化的转换就可以了
                    var newRow = JSON.parse(JSON.stringify(oldRow));
                    newRow.spec[columnName] = columnValue[j];
                    newList.push(newRow);
                }
            }
            return newList;
        }
        
    },

    //增加watch（监听器）
    watch:{
        //监听变量，entity.tbGoods.category1Id的变化，触发一个函数，发送请求，获取一级分类的下一级分类
        //'entity.tbGoods.category1Id'为要监听的变量，当发生变化时触发函数newcval表示新值
        //oldval为旧值
        'entity.tbGoods.category1Id':function (newval,oldval) {
            //赋值为空
            this.itemCat3List =[];
            //删除属性回到原始状态
            if(this.entity.tbGoods.id==null) {
                delete this.entity.tbGoods.category2Id;

                delete this.entity.tbGoods.category3Id;

                delete this.entity.tbGoods.typeTemplateId;
            }
            if(newval != undefined) {
                //根据分类的ID 查询该分类下的所有的子分类的列表
                axios.get('/itemCat/findByParentId/'+newval).then(function (response) {
                    app.itemCat2List = response.data
                }).catch(function (error) {
                    console.log("1231312131321");
                })
            }
        },

        //监听二级分类的变化，进行查询，再展示数据
        'entity.tbGoods.category2Id':function (newval,oldval) {
            //删除
            if(this.entity.tbGoods.id==null ) {
                delete this.entity.tbGoods.category3Id;
                delete this.entity.tbGoods.typeTemplateId;
            }

            if(newval != undefined) {
                //根据分类的ID 查询该分类下的所有的子分类的列表
                axios.get('/itemCat/findByParentId/'+newval).then(function (response) {
                    app.itemCat3List = response.data
                }).catch(function (error) {
                    console.log("1231312131321");
                })
            }
        },

        //监听三级分类的变化，获取三级分类的对象，对象里面有对应的typeId（模板的ID）
        'entity.tbGoods.category3Id':function (newval,oldval) {
            if(newval!=undefined){
                //根据分类的ID 查询该分类下的所有的子分类的列表
                axios.get('/itemCat/findOne/'+newval).then(
                    function (response) {//response.data= tbitemcat对象
                        //直接赋值 视图不会渲染
                        //app.entity.tbGoods.typeTemplateId=response.data.typeId;
                        //第一个参数 是指定给哪一个对象赋值
                        //第二个参数 是给指定的哪一个属性赋值
                        //第三个参数 指定的赋值的值是多少
                        //设置 值  并且视图会渲染
                        app.$set(app.entity.tbGoods,'typeTemplateId',response.data.typeId);

                    })
            }
        },

        //根据模板id监听变化，查询该模板的对象，对象里面有品牌列表数据，也就是查询tb_type_template表
        //获取品牌列表，获取里面的扩展属性的值
        'entity.tbGoods.typeTemplateId':function (newval, oldval) {
            if(newval != undefined) {
                axios.get('/typeTemplate/findOne/'+newval).then(function (response) {
                    //response.data就是typeTemplate对象
                    var typeTemplate = response.data;
                    //从后台返回的数据是字符串形式的，需要进行转换成json
                    app.brandTextList = JSON.parse(typeTemplate.brandIds);

                    if(app.entity.tbGoods.id==null) {
                        app.entity.goodsDesc.customAttributeItems =
                            JSON.parse(typeTemplate.customAttributeItems);
                    }
                })

                //在发送请求
                axios.get('/typeTemplate/findSpecList/'+newval).then(function (response) {
                    app.specList=response.data;
                })
            }
        },

        //监控变量的变化，
        'entity.tbGoods.isEnableSpec':function (newVal, oldVal) {
            //如果是隐藏规格列表，则清除所有数据，展开是在进行选择
            if(newVal==0) {
                this.entity.goodsDesc.specificationItems=[];
                this.entity.itemList=[];
            }
        },

        //监控数据变化 ，如果最后还剩下一个就直接删除
        'entity.itemList':function (newval,oldval) {
            //如果是相同的数据那么直接赋值为空即可
            console.log(JSON.stringify([{spec:{},price:0,num:0,status:'0',isDefault:'0'}])==JSON.stringify(newval));
            if(JSON.stringify([{spec:{},price:0,num:0,status:'0',isDefault:'0'}])==JSON.stringify(newval)){
                this.entity.itemList=[];
            }
        }
    },


    //钩子函数 初始化了事件和
    created: function () {
        this.findItemCatList();

        //使用插件中的方法getUrlParm() 返回的是一个JSON对象
        var request = this.getUrlParam();
        //获取参数值
        console.log(request);
        //根据id获取商品的信息
        this.findOne(request.id);
    }

})
