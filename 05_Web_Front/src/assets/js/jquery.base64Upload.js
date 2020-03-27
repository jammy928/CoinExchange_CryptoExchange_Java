(function ($) {
    $.fn.extend({
        "base64Upload":function(url,success){
            var isCanvasSupported = function (){
                var elem = document.createElement('canvas');
                return !!(elem.getContext && elem.getContext('2d'));
            };
            var readFile = function(el,callback){
                var reader = new FileReader();
                reader.onload = function(e){
                    var data = e.target.result;
                    
                    var file=el.files[0];
                    var imgSize=file.size;
                    if (data.lastIndexOf('data:base6+4') != -1) {
                        data = data.replace('data:base64', 'data:image/jpeg;base64');
                    } else if (data.lastIndexOf('data:,') != -1) {
                        data = data.replace('data:,', 'data:image/jpeg;base64,');
                    }
                     $('.modal').show();
                    /*if(imgSize > 1.6*1024*1024) {  
                        alert("请上传小于1.6M的文件");  
                        return;  
                     }*/

                      //--执行resize。  
                var _ir=ImageResizer({  
                        resizeMode:"auto"  
                        ,dataSource:data  
                        ,dataSourceType:"base64"  
                        ,maxWidth:1200 //允许的最大宽度  
                        ,maxHeight:600 //允许的最大高度。  
                        ,onTmpImgGenerate:function(img){  
  
                        }  
                        ,success:function(resizeImgBase64,canvas){
                            $.post(url,{base64Data:resizeImgBase64},function(resp){
                                if(typeof callback == 'function'){
                                callback(el,resp);
                                $('.modal').hide();
                            
                                }
                        });

                        }  
                        ,debug:true  
                });  


                   
                    
                    
                };
                reader.readAsDataURL(el.files[0]);
            }

            if(!isCanvasSupported()){
                alert("您的浏览器不支持");
                return false;
            }
            this.bind('change',function(event){
                var el = this;
                readFile(el,success);
            })
        }
    });
})(window.jQuery);